package com.vandenbreemen.mobilesecurestorage.file;

import com.vandenbreemen.mobilesecurestorage.data.ControlBytes;
import com.vandenbreemen.mobilesecurestorage.data.Pair;
import com.vandenbreemen.mobilesecurestorage.data.Serialization;
import com.vandenbreemen.mobilesecurestorage.file.api.FileDetails;
import com.vandenbreemen.mobilesecurestorage.file.api.FileType;
import com.vandenbreemen.mobilesecurestorage.log.SystemLog;
import com.vandenbreemen.mobilesecurestorage.log.slf4j.MessageFormatter;
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError;
import com.vandenbreemen.mobilesecurestorage.message.MSSRuntime;
import com.vandenbreemen.mobilesecurestorage.security.BytesToBits;
import com.vandenbreemen.mobilesecurestorage.security.SecureString;
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureDataUnit;
import com.vandenbreemen.mobilesecurestorage.util.NumberUtils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class IndexedFile {

    /**
     * Maximum chunk size in bytes.  Roughly 480 kb of data
     */
    public static final int CHUNK_SIZE = 160 * (3 * 1024);
    /**
     * From performance testing, the optimal chunk size (roughly 125 kb)
     */
    public static final int OPTIMAL_CHUNK_SIZE = 125 * (1000);
    private static final int MIN_FAT_INDEX = 2048;
    /**
     * Maximum wait time in milliseconds before ejecting the medium and killing the app
     * when lock failure occurs
     */
    private static final long MAX_LOCK_WAIT_MILLIS = 20000;
    /**
     * Indicates that storage failed because unit size too large.  Offer client code option to ask for a new unit
     */
    private static final String ATTR_NEW_UNIT_RQD = "NewUnitRequired";

    /**
     * Measuring sticks for making sure too many bytes don't get stored!
     */
    private static SecureDataUnit secureMeasuringStick;
    private static ChainedDataUnit chainedMeasuringStick;
    private static int emptySecMSSize;

    static {
        secureMeasuringStick = new SecureDataUnit();
        secureMeasuringStick.setLocationOfNextUnit(Long.MAX_VALUE);
        secureMeasuringStick.setKeyForNextUnit(new byte[32]);


        chainedMeasuringStick = new ChainedDataUnit();
        chainedMeasuringStick.setLocationOfNextUnit(Long.MAX_VALUE);

        emptySecMSSize = Serialization.toBytes(secureMeasuringStick).length;
    }

    /**
     * Switching this on will enable logging of various regular file IO functions
     */
    protected boolean testMode = false;
    /**
     * File allocation table
     */
    FAT fat;
    /**
     * The actual file on the file system
     *
     * @author kevin
     */
    private ChunkedFile file;
    private BytesToBits bytesToBits;
    /**
     * Desired size of each unit written
     */
    private int unitSize = CHUNK_SIZE;
    /**
     * Lock to prevent concurrent modification of the file system
     */
    private ReentrantReadWriteLock accessLock;

    /**
     * Cached data for files
     */
    private Cache<String, Object> fileCache;

    /**
     * Use this only for testing.
     */
    IndexedFile() {
        this.accessLock = new ReentrantReadWriteLock();
        this.bytesToBits = new BytesToBits();
        this.fileCache = new Cache2kBuilder<String, Object>() {
        }
                .entryCapacity(500)
                .eternal(true).build();
    }

    /**
     * Proper constructor to use.  This constructor will always load the fat
     *
     */
    public IndexedFile(File desiredFile) throws ChunkedMediumException {

        this();

        try {
            this.file = ChunkedFile.getChunkedFile(desiredFile);
        } catch (Exception ex) {
            throw new MSSRuntime("Unexpected error creating backing file", ex);
        }

        //	Now try and load up the FAT
        if (!this.file.isEmpty() /* IE the file wasn't just created */) {
            try {
                FileOnTheSystem fatLoader = new FileOnTheSystem(null, this, 0);
                byte[] fatBytes = fatLoader.getData();
                fat = (FAT) Serialization.deserializeBytes(fatBytes);
                fat.initialize();    //	Get transient stuff like access sem ready for use
            } catch (Exception ex) {
                SystemLog.get().error("Error loading FAT!", ex);
                throw new ChunkedMediumException("Unable to retrieve FAT", ex);
            }
        } else {    //	Otherwise create a fresh FAT
            SystemLog.get().debug("Creating FAT for new file");
            fat = new FAT();

            //	Immediately persist the fat
            storeFAT();
        }
    }

    /**
     * Kill the app and close the SFS
     */
    protected void errorOutOnLockTimeout() {

        this.close();

        SystemLog.get().error("THREAD={}  -  PERFORMING EMERGENCY CLEANUP.  HARD KILLING MEDIUM ACCESS", new Throwable(), Thread.currentThread().getName());
        try {
            emergencyCleanup();
        } catch (Exception ex) {
            SystemLog.get().error("FAILURE TO PERFORM CLEANUP!", ex);
        }

        throw new MSSRuntime("Lock acquisition failure.");

    }

    //	Allow visibility into this to allow for frawmework code to decide whether to allocate objects to other files

    /**
     * Perform any emergency cleanup operations needed
     */
    protected void emergencyCleanup() {
        //  To be executed by sub-types with security data
    }

    public int size(String fileName) {
        return fat._unitsAllocated(fileName).size();
    }

    /**
     * Gets the maximum size (in bytes) of a single unit that can be stored to this file
     *
     * @return
     */
    public final int getMaxUnitSizeBytes() {
        return getMaxSecureDataUnitSize();
    }

    /**
     * Gets the maximum size (in bytes) of a single unit that can be stored to this file
     *
     * @return
     */
    protected final int getMaxSecureDataUnitSize() {
        return getMaxPayloadSize() - emptySecMSSize;
    }

    int getMaxPayloadSize() {
        return (unitSize - (7 + 3) - 1);
    }

    /**
     * Store the FAT table to the file system
     *
     * @return
     */
    private void storeFAT() {

        parseAndWriteBytes(FAT.FILENAME, (long) getMaxPayloadSize() - MIN_FAT_INDEX, Serialization.toBytes(fat));

    }

    /**
     * Overridable logic for creating a new chained data unit
     */
    protected ChainedUnit getChainedDataUnit() {
        return new ChainedDataUnit();
    }

    /**
     * Updates the given data unit on the file
     *
     * @param fileName
     * @param index
     * @param unit
     */
    final void updateDataUnit(String fileName, long index, ChainedUnit unit) {
        try {
            if (!accessLock.writeLock().tryLock(MAX_LOCK_WAIT_MILLIS, TimeUnit.MILLISECONDS))
                errorOutOnLockTimeout();

            if (!fat._unitsAllocated(fileName).contains(index))
                throw new IllegalArgumentException("Unit '" + index + "' not allocated to file '" + fileName + "'!");

            writeDataUnit(index, unit);

        } catch (InterruptedException in) {
            errorOutOnLockTimeout();
            Thread.currentThread().interrupt();
        } finally {
            accessLock.writeLock().unlock();
        }
    }

    /**
     * This method is kept private intentionally as it is only for use in files you want to explore
     * {@link FileAllocationView in chunks}.  Thus your application code should be using the {@link FileAllocationView}
     * for managing such files!
     *
     * @param fileName
     * @param unit
     * @return Index where the unit was stored
     */
    final long addDataUnit(String fileName, ChainedUnit unit) {

        //	Get the next available unit index we can allocate units to
        long nextUnit = fat.nextAvailableUnitIndex();

        //	If the file has no units (ie touch() was called to create it rather than one of the store() methods)
        if (CollectionUtils.isEmpty(fat._unitsAllocated(fileName))) {

            writeDataUnit(nextUnit, unit);

            fat._addUnitFor(fileName, nextUnit);
            storeFAT();
            return nextUnit;
        }

        //	First get the last unit allocated to the file
        List<Long> currentlyAllocated = fat._unitsAllocated(fileName);
        ChainedUnit latestUnit = readDataUnit(currentlyAllocated.get(currentlyAllocated.size() - 1));


        //	Tell the last unit used for the file about the new unit we're about to allocate
        latestUnit.setLocationOfNextUnit(nextUnit);

        //	Update the last unit in the FS so the FS knows about the arrangement of units for the file
        writeDataUnit(currentlyAllocated.get(currentlyAllocated.size() - 1), latestUnit);

        //	Reserve the new unit in the FAT
        fat._addUnitFor(fileName, nextUnit);

        //	And finally write the unit!
        writeDataUnit(nextUnit, unit);

        //	And finally write out the FAT changes
        storeFAT();

        return nextUnit;

    }

    /**
     * Store the data for the given file.  Bytes will be written destructively, so that the array will no longer be usable!
     *
     * @param fileName Where the data is to be stored
     */
    private void parseAndWriteBytes(String fileName, long maxChunkLength, byte[] bytes) {
        if (maxChunkLength > getMaxPayloadSize())
            throw new MSSRuntime("Cannot store chunks longer than " + maxChunkLength + " bytes!");

        try {

            if (!accessLock.writeLock().tryLock(MAX_LOCK_WAIT_MILLIS, TimeUnit.MILLISECONDS)) {
                errorOutOnLockTimeout();
            }

            fileCache.containsAndRemove(fileName);

            //	Now we need to know what units we can write the data to!
            List<Long> unitsToAllocate = null;
            if (!FAT.FILENAME.equals(fileName)) {
                if (fat._exists(fileName))
                    unitsToAllocate = fat._unitsAllocated(fileName);
            } else    //	If the file is the FAT file then it most certainly DOES exist!
                unitsToAllocate = fat._unitsAllocated(fileName);    //	Otherwise it IS the FAT so we can get its units

            Long nextAvailUnit = !CollectionUtils.isEmpty(unitsToAllocate) ? unitsToAllocate.remove(0) :
                    fat.nextAvailableUnitIndex();


            if (bytes.length <= maxChunkLength) {
                writeToSingleChunk(fileName, bytes, nextAvailUnit);
            } else {

                doMultiUnitDataWrite(fileName, maxChunkLength, bytes, unitsToAllocate, nextAvailUnit);
            }

            //	Now that we've finished writing the data update the units that are usable in case the file was shortened
            boolean removedUnitsFromFAT = removeUnusedUnits(fileName, unitsToAllocate);

            if (!FAT.FILENAME.equals(fileName) || removedUnitsFromFAT)    //	Write the updated FAT table now that everything has been written.
                storeFAT();

        } catch (InterruptedException inter) {
            errorOutOnLockTimeout();
            Thread.currentThread().interrupt();
        } finally {
            accessLock.writeLock().unlock();
        }
    }

    private void doMultiUnitDataWrite(String fileName, long maxChunkLength, byte[] bytes, List<Long> unitsToAllocate, Long nextAvailUnit) {
        ChainedUnit unit;
        if (testMode)
            SystemLog.get().debug("file:  {}:  data spans multiple chunks", fileName);

        boolean keepAddingChunks = true;
        int i = 0;    //	Current byte index
        byte[] unitData;    //	Bytes to store to a single unit
        do {
            unitData = new byte[(int) maxChunkLength];
            System.arraycopy(bytes, i, unitData, 0, (int) maxChunkLength);

            if (testMode)
                SystemLog.get().debug("file:  {}:  allocating to unit {}", fileName, nextAvailUnit.longValue());

            unit = getChainedDataUnit();
            unit.setData(unitData);

            fat._addUnitFor(fileName, nextAvailUnit);

            long locationToWrite = nextAvailUnit.longValue();
            nextAvailUnit = determineNextAvailUnit(bytes, unitsToAllocate, nextAvailUnit, unit, i);
            writeDataUnit(locationToWrite, unit);



            //			Keep going as long as remaining bytes size is greater than max chunk size
            if (bytes.length - i > maxChunkLength)
                i += maxChunkLength;    //	Move another chunk segment along the array

            //	If after moving up one segment we're now one chunk or less from the end then stop.
            if (bytes.length - i <= maxChunkLength)
                keepAddingChunks = false;    //	Otherwise stop
        } while (keepAddingChunks);

        //	Now store the remainder
        if (bytes.length - i > 0) {    //	If there are residual bytes
            writeRemainingData(fileName, bytes, nextAvailUnit, i);
        }
    }

    /**
     * Given unit to be created, works out next avail unit if one is needed.  Note that this method will also update
     * the given ChainedUnit with that next unit.
     *
     * @param bytes
     * @param unitsToAllocate
     * @param nextAvailUnit
     * @param unit             Unit being written
     * @param currentByteIndex
     * @return
     */
    private Long determineNextAvailUnit(byte[] bytes, List<Long> unitsToAllocate, Long nextAvailUnit, ChainedUnit unit, int currentByteIndex) {
        if ((long) (bytes.length - currentByteIndex) > 0 /* If from the current position along the original bytes array is still within the array */) {
            //	Determine next available unit
            nextAvailUnit = !CollectionUtils.isEmpty(unitsToAllocate) ? unitsToAllocate.remove(0) :
                    fat.nextAvailableUnitIndex();
            unit.setLocationOfNextUnit(nextAvailUnit);
        }
        return nextAvailUnit;
    }

    /**
     * Given known list of units that were allocated, remove these from file.
     *
     * @param fileName
     * @param noLongerNeededUnits
     * @return  true if unused units were removed from the FAT (thus necessitating storing FAT once again!)
     */
    private boolean removeUnusedUnits(String fileName, List<Long> noLongerNeededUnits) {
        if (!CollectionUtils.isEmpty(noLongerNeededUnits)) {
            try {
                fat.accessLock();
                noLongerNeededUnits.forEach(u -> fat._removeUnitFor(fileName, u));

                return FAT.FILENAME.equals(fileName);

            } finally {
                fat.releaseAccessLock();
            }
        }

        return false;
    }

    /**
     * Write out remaining data when multiple units/chunks were required to store the data
     * Write out remaining data when multiple units/chunks were required to store the data
     *
     * @param fileName
     * @param bytes
     * @param nextAvailUnit
     * @param currentByteIndex
     */
    private void writeRemainingData(String fileName, byte[] bytes, Long nextAvailUnit, int currentByteIndex) {
        byte[] unitData;
        ChainedUnit unit;
        int residueVectorSize = bytes.length - currentByteIndex;
        unitData = new byte[residueVectorSize];
        System.arraycopy(bytes, currentByteIndex, unitData, 0, residueVectorSize);

        if (testMode)
            SystemLog.get().debug("file:  " + fileName + ":  allocating to unit " + nextAvailUnit.longValue());

        unit = getChainedDataUnit();
        unit.setData(unitData);

        fat._addUnitFor(fileName, nextAvailUnit);

        writeDataUnit(nextAvailUnit.longValue(), unit);
    }

    /**
     * Write the given data to specified chunk/unit
     *
     * @param fileName
     * @param bytes
     * @param unitIndex
     */
    private void writeToSingleChunk(String fileName, byte[] bytes, Long unitIndex) {
        ChainedUnit unit;

        unit = getChainedDataUnit();
        unit.setData(bytes);

        writeDataUnit(unitIndex.longValue(), unit);

        fat._addUnitFor(fileName, unitIndex);
    }

    public final void storeObject(String fileName, Serializable object) {

        byte[] serialized = Serialization.toBytes(object);
        parseAndWriteBytes(fileName, getMaxPayloadSize() - 2048l/* Padding for encryption type stuff */, serialized);
    }

    /**
     * Create a new file if file does not exist.  If file does exist this method will do nothing
     *
     * @param fileName
     */
    public void touch(String fileName) {
        fat._touch(fileName);
        storeFAT();
    }

    /**
     * Writes the given bytes to the location of the current chunk.  This should NEVER be used outside of tests or internal logic!  Note that overriding
     * this method will be necessary in order to encrypt data units!  This method is NOT thread-safe.  Before calling this ensure you have
     * locked the {@link #accessLock}'s write lock first!
     */
    protected final void writeBytes(long chunkIndex, byte[] bytes) {
        file.writeBytes(chunkIndex * unitSize, encodeChunk(bytes));
    }

    /**
     * Encodes a chunk of data with a descriptor for its length
     *
     * @param bytes         bytes to encode
     * @return
     */
    protected final byte[] encodeChunk(byte[] bytes) {

        if (bytes.length > getMaxPayloadSize())
            throw new MSSRuntime("Size of bytes exceeds maximum size of " + (unitSize - (7 + 3))).setAttribute(ATTR_NEW_UNIT_RQD, "RQD");

        //	Total size of byte array that will be written out as a chunk
        int totalSize = 3;    //	At least control bytes.

        //  Convert the length to bytes
        Long lengthLong = Long.valueOf(bytes.length);
        ByteBuffer lenBuffer = ByteBuffer.allocate(Long.BYTES);
        lenBuffer.putLong(lengthLong);
        byte[] lengthBytes = lenBuffer.array();

        totalSize += lengthBytes.length;
        totalSize += bytes.length;

        byte[] ret = new byte[totalSize];
        ret[0] = ControlBytes.START_OF_MEDIUM;
        ret[1] = ControlBytes.LENGTH_IND;
        int j = 2;
        for (int i = 0; i < lengthBytes.length; i++) {
            ret[i + 2] = lengthBytes[i];
            j++;
        }

        ret[j] = ControlBytes.START_OF_CONTENT;
        j++;

        System.arraycopy(bytes, 0, ret, j, bytes.length);
        return bytesToBits.zeroPad(ret, unitSize);
    }

    /**
     * Gets the index of the content to read relative to start of the given byte array along with the
     * length of the chunk itself.
     *
     * @param bytes
     * @return
     */
    final byte[] readChunk(byte[] bytes) {
        if (bytes.length <= 4)
            throw new MSSRuntime("Unexpected:  Chunk should contain start of medium, length indicator, at least one length byte, a start of content and some data!");

        if (bytes[0] != ControlBytes.START_OF_MEDIUM)
            throw new MSSRuntime("Unexpected.  First byte of data was not the start of medium byte!");
        if (bytes[1] != ControlBytes.LENGTH_IND)
            throw new MSSRuntime("Unexpected.  Second byte of data was not length indicator!");
        if (bytes[2] == ControlBytes.START_OF_CONTENT)
            throw new MSSRuntime("Unexpected.  Size bytes should appear after length indicator.");

        byte[] szBytes = new byte[Long.BYTES];

        int idxStartOfContent = 2;
        for (int i = 0; i < Long.BYTES; i++) {
            szBytes[i] = (bytes[2 + i]);
            idxStartOfContent = 2 + i;
        }

        //  Read in the length
        ByteBuffer lengthBuf = ByteBuffer.allocate(Long.BYTES);
        lengthBuf.put(szBytes);
        lengthBuf.flip();
        Long length = lengthBuf.getLong();

        //	Add 1 for the start of content byte itself and then 1 to get to the first byte of content!
        idxStartOfContent += 2;

        byte[] chunkContent = new byte[length.intValue()];
        System.arraycopy(bytes, idxStartOfContent, chunkContent, 0, length.intValue());

        return chunkContent;

    }

    /**
     * Overridable logic for reading in a {@link ChainedUnit chained data unit}.  Pay close attention to the
     * implementation of {@link #getChainedDataUnit()} when implementing!  Overriding this will ALSO be necessary
     * when dealing with encrypted data units!
     *
     * @return
     */
    protected ChainedUnit readDataUnit(long chunkIndex) {
        Chunk chunk = readChunk(chunkIndex);
        return (ChainedUnit) Serialization.deserializeBytes(chunk.getBytes());
    }

    /**
     * Logic for writing out a data unit.  Override for providing encryption.
     *
     * @param dataUnit
     */
    protected void writeDataUnit(long chunkIndex, ChainedUnit dataUnit) {
        writeBytes(chunkIndex, Serialization.toBytes(dataUnit));
    }

    /**
     * Reads the current chunk from the file
     */
    protected final Chunk readChunk(long chunkIndex) {
        byte[] bytes = file.readBytes(chunkIndex * unitSize, unitSize);
        return new Chunk(this, readChunk(bytes));
    }

    /**
     * List all files on this file system
     */
    public final List<String> listFiles() {
        return fat.listFiles();
    }

    /**
     * Returns whether the file of the given name exists
     *
     * @param fileName
     * @return
     */
    public final boolean exists(String fileName) {
        return fat._exists(fileName);
    }

    /**
     * Load the file with the given name from the file system
     *
     * @param fileName
     * @return
     * @throws ChunkedMediumException If the file does not exist
     */
    public final Object loadFile(String fileName) throws ChunkedMediumException {
        byte[] data = doGetBytesForObjectFile(fileName);
        return Serialization.deserializeBytes(data);

    }

    /**
     * Loads the raw bytes for a file that was stored using {@link #storeObject(String, Serializable)}
     *
     * @param fileName
     * @return
     * @throws ChunkedMediumException
     */
    private byte[] doGetBytesForObjectFile(String fileName) throws ChunkedMediumException {
        byte[] data;
        try {

            if (!accessLock.readLock().tryLock(MAX_LOCK_WAIT_MILLIS, TimeUnit.MILLISECONDS))
                errorOutOnLockTimeout();

            if (!fat._exists(fileName))
                throw new ChunkedMediumException("No such file as '{}' exists on the medium", fileName).setType(ChunkedMediumException.TYPE.FILE_NOT_FOUND);


            List<Long> unitsAllocated = fat._unitsAllocated(fileName);

            FileOnTheSystem onTheSystem = new FileOnTheSystem(new SecureString(fileName.getBytes()), this, unitsAllocated.get(0).intValue());
            data = onTheSystem.getData();
        } catch (InterruptedException inte) {
            errorOutOnLockTimeout();
            Thread.currentThread().interrupt();
            throw new ChunkedMediumException("Interrupted", inte);
        } finally {
            accessLock.readLock().unlock();
        }
        return data;
    }

    /**
     * Gets a {@link FileAllocationView view} for working with the given file
     *
     * @param fileName
     * @return
     */
    public final FileAllocationView getFileView(String fileName) {
        return new FileAllocationView(this, fileName);
    }

    /**
     * Delete the file with the given name
     *
     * @throws ChunkedMediumException If the file does not exist
     */
    public final void deleteFile(String fileName) throws ChunkedMediumException {
        deleteFiles(fileName);
    }

    /**
     * Delete one or more files
     *
     * @param fileNames
     */
    public final void deleteFiles(String... fileNames) throws ChunkedMediumException {

        Set<String> fileSet = new HashSet<>();

        List<String> missingFiles = new ArrayList<>();
        if (!ArrayUtils.isEmpty(fileNames)) {
            Arrays.stream(fileNames).forEach(filename -> {
                if (!fat._exists(filename)) {
                    missingFiles.add(filename);
                }
                fileSet.add(filename);
            });

            if (fileNames.length != fileSet.size()) {
                throw new ChunkedMediumException("Cannot delete same file more than once files to delete were " + Arrays.asList(fileNames));
            }
        }


        if (!CollectionUtils.isEmpty(missingFiles)) {
            throw new ChunkedMediumException("The following files are not present on the medium:\n" + missingFiles);
        }

        try {
            if (!accessLock.writeLock().tryLock(MAX_LOCK_WAIT_MILLIS, TimeUnit.MILLISECONDS))
                errorOutOnLockTimeout();
            fat.accessLock();
            Arrays.stream(fileNames).forEach(fat::_delete);

            //  Now optimize the file system!
            optimizeFileSystem();

            storeFAT();

        } catch (InterruptedException e) {
            errorOutOnLockTimeout();
            Thread.currentThread().interrupt();
        } finally {
            fat.releaseAccessLock();
            accessLock.writeLock().unlock();
        }

    }

    private void optimizeFileSystem() {

        long numOptimizations = fat._totalUnused();
        for (int i = 0; i < numOptimizations; i++) {
            Optional<FAT.UnitShuffle> optimization = fat._nextShuffle();
            optimization.ifPresent(this::reallocateUnit);

            this.fat.trim();
            long unitIndexToTrimTo = fat.maxAllocatedIndex() + 1;
            if (testMode) {
                SystemLog.get().debug("Trimming File Length to Max Idx = {}", unitIndexToTrimTo);
            }
            this.file.updateLength(unitIndexToTrimTo * CHUNK_SIZE);

            //  Stop if no more free units
            if (fat._totalUnused() == 0) {
                break;
            }
        }
    }

    private void reallocateUnit(FAT.UnitShuffle shuffle) {
        if (testMode) {
            SystemLog.get().debug("POST DELETE OPTIMIZATION:  {}", shuffle);
        }

        ChainedUnit from = readDataUnit(shuffle.getSourceIndex());

        if (testMode) {
            SystemLog.get().debug("LOAD FROM - nxtUnit = {}", from.getLocationOfNextUnit());
        }

        if (shuffle.getIncomingReferenceUnit() > -1) {
            ChainedUnit incomingReference = readDataUnit(shuffle.getIncomingReferenceUnit());

            if (testMode) {
                SystemLog.get().debug("Update Chain at {}, from {} to {}", shuffle.getIncomingReferenceUnit(), incomingReference.getLocationOfNextUnit(), shuffle.getDestinationIndex());
            }

            if (incomingReference.getLocationOfNextUnit() != shuffle.getSourceIndex()) {
                throw new MSSRuntime("Unexpected:  Incoming Ref Next = " + incomingReference.getLocationOfNextUnit() + ", expected " + shuffle.getSourceIndex());
            }

            incomingReference.setLocationOfNextUnit(shuffle.getDestinationIndex());
            writeDataUnit(shuffle.getIncomingReferenceUnit(), incomingReference);
        }
        writeDataUnit(shuffle.getDestinationIndex(), from);
        fat.updateUnitPlacement(shuffle.getSourceIndex(), shuffle.getDestinationIndex());
    }

    /**
     * Import the given file's bytes and stores it to this {@link IndexedFile}.
     *
     * @param onDisk       Location of the file on disk to import
     * @return Name given to imported file
     */
    public final String importFile(File onDisk) throws ApplicationError {

        String fileName = getFileNameGeneratedForImport(onDisk);

        ImportedFileData imported = FileImporterJavaInteractor.getFileImporter().loadFile(onDisk);
        storeObject(fileName, imported);

        return fileName;
    }

    /**
     * Get the default file name that will be generated if you import the given file
     *
     * @param onDisk
     * @return
     */
    public final String getFileNameGeneratedForImport(File onDisk) {
        return onDisk.getName();
    }

    /**
     * Load the raw bytes from a file known to be stored as a {@link ImportedFileData}.  Please use this
     * method only when you can be confident that the original file was stored using {@link #importFile(File)}.
     *
     * @param fileName Name of file in this file system
     * @return
     */
    public final byte[] loadBytesFromFile(String fileName) throws ChunkedMediumException {
        Object obj = loadFile(fileName);
        if (obj instanceof ImportedFileData) {
            return ((ImportedFileData) obj).getFileData();
        } else
            throw new ChunkedMediumException("File is not an " + ImportedFileData.class.getSimpleName() + " but is instead a " + obj.getClass());
    }

    /**
     * Gets the total number of units on this file system
     *
     * @return
     */
    protected final long getTotalUnits() {

        FileAllocationView fatView = getFileView(FAT.FILENAME);

        return fat.getTotalUnits() + fatView.getUnits().size();
    }

    /**
     * Visit all data units on this SFS with the given visitor.  Use with caution!  This method will prevent anything
     * else from accessing the file system while it is running!
     *
     * @param visitor
     */
    public final void visitDataUnits(Consumer<Pair<Long, ChainedUnit>> visitor) {
        long totalUnits = getTotalUnits();
        try {
            if (!accessLock.writeLock().tryLock(MAX_LOCK_WAIT_MILLIS, TimeUnit.MILLISECONDS))
                errorOutOnLockTimeout();
            for (long i = 0; i < totalUnits; i++) {
                ChainedUnit unit = readDataUnit(i);
                visitor.accept(new Pair<Long, ChainedUnit>(i, unit));
            }
        } catch (InterruptedException in) {
            errorOutOnLockTimeout();
            Thread.currentThread().interrupt();
        } finally {
            accessLock.writeLock().unlock();
        }
    }

    @Override
    public final Object clone() throws CloneNotSupportedException {    //	NOSONAR
        throw new CloneNotSupportedException();
    }

    private final void writeObject(ObjectOutputStream out)
            throws java.io.IOException {
        throw new java.io.IOException("Object cannot be serialized");
    }

    private final void readObject(ObjectInputStream in)
            throws java.io.IOException {
        throw new java.io.IOException("Class cannot be deserialized");
    }

    /**
     * Close the file system
     */
    public void close() {
        if (fileCache != null) {
            fileCache.clear();
            fileCache.clearAndClose();
            fileCache = null;
        }
        if (this.fat != null) {
            this.fat.close();
            this.fat = null;
        }
    }

    @Override
    protected final void finalize() {

        close();
    }

    /**
     * Rename a file
     *
     * @param currentName
     * @param newName
     */
    public final void rename(String currentName, String newName) {
        if (!fat._exists(currentName))
            throw new MSSRuntime("No such file as '" + currentName + "'");
        if (fat._exists(newName))
            throw new MSSRuntime("File '" + newName + "' already exists");

        try {
            if (!accessLock.writeLock().tryLock(MAX_LOCK_WAIT_MILLIS, TimeUnit.MILLISECONDS)) {
                errorOutOnLockTimeout();
            }

            fat._rename(currentName, newName);
            storeFAT();
        } catch (InterruptedException inter) {
            errorOutOnLockTimeout();
            Thread.currentThread().interrupt();
        } finally {
            accessLock.writeLock().unlock();
        }

    }

    /**
     * Load the given file, caching it if it has not already been cached
     *
     * @param fileName
     * @return
     */
    public Object loadAndCacheFile(String fileName) {
        return this.fileCache.computeIfAbsent(fileName, () -> Serialization.deserializeBytes(doGetBytesForObjectFile(fileName)));
    }

    public byte[] loadAndCacheBytesFromFile(String fileName) throws ChunkedMediumException {
        Object obj = loadAndCacheFile(fileName);
        if (obj instanceof ImportedFileData) {
            return ((ImportedFileData) obj).getFileData();
        } else
            throw new ChunkedMediumException("File is not an " + ImportedFileData.class.getSimpleName() + " but is instead a " + obj.getClass());
    }

    public final void setFileType(String fileName, FileType fileType) {
        try {
            if (!accessLock.writeLock().tryLock(MAX_LOCK_WAIT_MILLIS, TimeUnit.MILLISECONDS))
                errorOutOnLockTimeout();
            fat.fileDetails(fileName).setFileMeta(new FileMeta(fileType));
            storeFAT();
        } catch(InterruptedException in){
            errorOutOnLockTimeout();
            Thread.currentThread().interrupt();
        }
        finally {
            accessLock.writeLock().unlock();
        }

    }

    public final FileDetails getDetails(String fileName) throws ChunkedMediumException {

        if(!exists(fileName)){
            throw new ChunkedMediumException("File "+fileName + " does not exist");
        }

        try {
            if(!accessLock.readLock().tryLock(MAX_LOCK_WAIT_MILLIS, TimeUnit.MILLISECONDS))
                errorOutOnLockTimeout();
            return fat.fileDetails(fileName);
        } catch (InterruptedException in){
            errorOutOnLockTimeout();
            Thread.currentThread().interrupt();
            throw new MSSRuntime("Failed to read file details", in);
        }
        finally {
            accessLock.readLock().unlock();
        }
    }

    /**
     * Interface that allows client code to make updates to a specific unit at a specific location in the {@link IndexedFile}
     * without needing to worry about where that location is.
     *
     * @author kevin
     */
    public static interface UnitUpdateAdapter {
        /**
         * Update the location in the file with the new unit
         *
         * @param unit New unit to set the content as
         */
        public void update(ChainedUnit unit) throws NewUnitNeededException;
    }

    /**
     * Exception thrown indicating that the size of a unit has exceeded maximum size and so a new unit is needed
     *
     * @author kevin
     */
    public static class NewUnitNeededException extends Exception {

        /**
         *
         */
        private static final long serialVersionUID = 7563863325545879811L;


    }

    /**
     * For use on files known to span multiple units and whose processing must not put a strain on memory.  Note that
     * this view will only allow you to iterate over {@link ChainedUnit} chained data units
     *
     * @author kevin
     */
    public static class FileAllocationView implements Iterator<ChainedUnit> {

        private IndexedFile fileSystem;

        /**
         * Where the current units are indexed
         */
        private List<Long> unitIndexes;

        /**
         * Private iterator for iterating along unit indexes
         */
        private Iterator<Long> unitIndexItr;

        /**
         * Name of the file on the file system
         */
        private String fileName;

        private FileAllocationView(IndexedFile fileSystem, String fileName) {
            this.fileSystem = fileSystem;
            this.unitIndexes = fileSystem.fat._unitsAllocated(fileName);
            this.unitIndexItr = unitIndexes.iterator();
            this.fileName = fileName;
        }

        @Override
        public final boolean hasNext() {
            return unitIndexItr.hasNext();
        }

        /**
         * Provide access to the file system this file view peers into.  Be careful with this!
         *
         * @return
         */
        public final IndexedFile getMainFileSystem() {
            return this.fileSystem;
        }

        @Override
        public final ChainedUnit next() {
            return fileSystem.readDataUnit(unitIndexItr.next());
        }

        @Override
        public final void remove() {
            SystemLog.get().error("Removing units not supported");
        }

        /**
         * Allocates a new data unit to the file.  This will reset the {@link #unitIndexes}s for any code that is iterating
         *
         * @param unit
         * @return Index where new unit was stored
         */
        public final long addUnit(ChainedUnit unit) {
            try {

                if (!fileSystem.accessLock.writeLock().tryLock(MAX_LOCK_WAIT_MILLIS, TimeUnit.MILLISECONDS))
                    fileSystem.errorOutOnLockTimeout();

                long ret = fileSystem.addDataUnit(fileName, unit);

                //	Reinitialize unit iteration!
                this.unitIndexes = fileSystem.fat._unitsAllocated(fileName);
                this.unitIndexItr = unitIndexes.iterator();

                return ret;
            } catch (InterruptedException inter) {
                fileSystem.errorOutOnLockTimeout();
                Thread.currentThread().interrupt();
                return 0;
            } finally {
                fileSystem.accessLock.writeLock().unlock();
            }
        }

        /**
         * Update the data stored in the given unit
         *
         * @param index
         * @param unit
         */
        public final void updateUnit(long index, ChainedUnit unit) {
            if (!unitIndexes.contains(index))
                throw new IllegalArgumentException(MessageFormatter.arrayFormat("Unit index '{}' not allocated to the file", new Object[]{Long.toString(index)}).getMessage());

            fileSystem.updateDataUnit(fileName, index, unit);
        }

        /**
         * Gets updater for making updates to a specific unit
         *
         * @param index
         * @return
         */
        public final UnitUpdateAdapter getUnitUpdater(final long index) {
            if (!unitIndexes.contains(index))
                throw new IllegalArgumentException("Unit index '" + index + "' not allocated to the file");

            //	Otherwise return an adapter that allows for updating
            return unit -> {
                try {
                    updateUnit(index, unit);
                } catch (MSSRuntime krx) {
                    if (krx.getAttribute(ATTR_NEW_UNIT_RQD) != null)    //	Send client code instruction to ask for a new unit
                        throw new NewUnitNeededException();
                    else    //	Otherwise send the error up the stack as a regular runtime error
                        throw krx;
                }
            };
        }

        /**
         * Read the unit at the given index.  This method will throw an error if the requested index is not
         * actually allocated to the file this view is for!
         *
         * @param index
         * @return
         */
        public final ChainedUnit readUnit(long index) {


            try {

                if (!fileSystem.accessLock.readLock().tryLock(MAX_LOCK_WAIT_MILLIS, TimeUnit.MILLISECONDS))
                    fileSystem.errorOutOnLockTimeout();

                if (!unitIndexes.contains(index))
                    throw new IllegalArgumentException("Unit index '" + index + "' not allocated to the file");

                return fileSystem.readDataUnit(index);
            } catch (InterruptedException in) {
                fileSystem.errorOutOnLockTimeout();
                Thread.currentThread().interrupt();
                return null;
            } finally {
                fileSystem.accessLock.readLock().unlock();
            }
        }

        /**
         * Gets a list of unit indexes currently allocated to the file
         *
         * @return
         */
        public final List<Long> getUnits() {
            return new ArrayList<>(this.unitIndexes);
        }
    }

    /**
     * File stored on this file system.  Note that this class will chain together all units of the file
     * irrespective of the contents of the file allocation table.  The reaosn for this is simply that the
     * FAT itself is stored on the medium as another collection of one or more units!
     *
     * @author kevin
     */
    public static class FileOnTheSystem {

        /**
         * The file system this file resides on
         */
        private IndexedFile fileSystem;

        /**
         * Location of first chunk
         */
        private long startChunk;

        /**
         * Name of this file
         */
        private SecureString fileName;

        private FileOnTheSystem(SecureString fileName, IndexedFile file, int startChunk) {
            this.fileName = fileName;
            this.fileSystem = file;
            this.startChunk = startChunk;
            if (fileSystem.testMode) {
                SystemLog.get().debug("File {}:  Start chunk idx:  {}, indexes={}", fileName.toString(), startChunk, file.fat._unitsAllocated(fileName.toString()));
            }
        }

        private final byte[] getData() {

            List<ChainedUnit> units = new ArrayList<>();
            ChainedUnit unit = fileSystem.readDataUnit(startChunk);

            units.add(unit);

            while (!NumberUtils.empty(unit.getLocationOfNextUnit())) {

                if (fileSystem.testMode) {
                    SystemLog.get().debug("File {}:  next chunk idx:  {}", fileName.toString(), unit.getLocationOfNextUnit());
                }

                unit = fileSystem.readDataUnit(unit.getLocationOfNextUnit());
                units.add(unit);
            }


            if (!NumberUtils.empty(units.get(units.size() - 1).getLocationOfNextUnit()))
                throw new MSSRuntime("Unexpected.  Final unit for file '" + fileName + "' declares a next data unit!  Suspect corrupted file!");

            SecureString byteBuffer = new SecureString();
            for (ChainedUnit u : units) {
                if (u.getData() == null) {
                    SystemLog.get().error("One or more units are missing data");
                    continue;
                }
                byteBuffer.addBytes(u.getData());
            }

            return byteBuffer.copyBytes();
        }
    }

    //	File management stuff.  Read-only methods go here!

    /**
     * Chunk of data from the file
     *
     * @author kevin
     */
    public static class Chunk {

        private byte[] bytes;

        /**
         * Encapsulating file
         */
        private IndexedFile ifile;

        /**
         * New Chunk for storing data to the file
         */
        private Chunk(IndexedFile ifile) {
            this.ifile = ifile;
        }

        private Chunk(IndexedFile ifile, byte[] bytes) {
            this(ifile);
            this.bytes = bytes;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public final void setBytes(byte[] bytes) {
            if (bytes.length > ifile.getMaxPayloadSize())
                throw new MSSRuntime("Size of bytes exceeds maximum size of " + (ifile.unitSize - (7 + 3)));
            this.bytes = bytes;
        }

    }

}
