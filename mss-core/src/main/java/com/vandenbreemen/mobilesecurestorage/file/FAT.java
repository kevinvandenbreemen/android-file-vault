package com.vandenbreemen.mobilesecurestorage.file;

import com.vandenbreemen.mobilesecurestorage.file.api.FileDetails;
import com.vandenbreemen.mobilesecurestorage.log.SystemLog;
import com.vandenbreemen.mobilesecurestorage.message.MSSRuntime;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * <h2>Intro</h2>
 * <p>Serializable "File Allocation Table" to indicate which units are used for which files as well
 * as which units are free as a result of file deletion!
 * <p/>Note that this is not a java implementation of Microsoft's FAT.  I simply borrow the term to
 * indicate a "table" mapping file names to their corresponding 'units' of bytes on the file.
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class FAT implements Serializable {




    static class UnitShuffle {

        private final long sourceIndex;
        private final long destinationIndex;
        private long incomingReferenceUnit = -1;

        UnitShuffle(long sourceIndex, long destinationIndex) {
            this.sourceIndex = sourceIndex;
            this.destinationIndex = destinationIndex;
        }

        @Override
        public String toString() {
            StringBuilder bld = new StringBuilder("UNIT SHUFFLE:  ");
            bld.append("src=").append(sourceIndex).append(", dst=").append(destinationIndex);
            if(incomingReferenceUnit > -1) {
                bld.append(", refInc=").append(incomingReferenceUnit);

            }
            return bld.toString();
        }

        public long getSourceIndex() {
            return sourceIndex;
        }

        public long getDestinationIndex() {
            return destinationIndex;
        }

        public long getIncomingReferenceUnit() {
            return incomingReferenceUnit;
        }

        void setIncomingReferenceUnit(long incomingReferenceUnit) {
            this.incomingReferenceUnit = incomingReferenceUnit;
        }
    }

    /**
     * Special reserved filename
     */
    public static final String FILENAME = "____FAT%MMVDIRECTION_JIBBER";
    /**
     *
     */
    private static final long serialVersionUID = 7715454070438785854L;
    /**
     * Access lockdown
     */
    private transient ReentrantReadWriteLock accessLock;

    /**
     * Total number of units on the file system
     */
    private long totalUnits;

    /**
     * Which units have been freed up as a result of a file deletion
     */
    private List<Long> freeUnitIndexes;

    /**
     * Mapping from file name to list of units allocated to that file.
     */
    private Map<String, List<Long>> fileAllocations;

    /**
     * Metadata (optional) for files
     */
    private Map<String, FileDetails> fileMetadata;

    public FAT() {
        this.accessLock = new ReentrantReadWriteLock();
        this.freeUnitIndexes = new LinkedList<>();
        this.fileAllocations = new HashMap<>();
        this.fileMetadata = new HashMap<>();
        this.totalUnits = 0;

        //	Unit allocation for the FAT
        fileAllocations.put(FILENAME, new ArrayList<Long>(Arrays.asList(0l /*First chunk of a file system is for FAT ALWAYS!*/)));
    }

    /**
     * Initialize FAT for use after recovery from a file
     */
    final void initialize() {
        this.accessLock = new ReentrantReadWriteLock();
    }

    /**
     * Adds the given unit index to the given file.  If the unit is already allocated then this method will do nothing.
     *
     * @param fileName
     */
    final void _addUnitFor(String fileName, long unitIndex) {
        try {
            accessLock.writeLock().lock();
            if (!fileAllocations.containsKey(fileName)) {
                List<Long> indexes = new ArrayList<>();
                fileAllocations.put(fileName, indexes);
            }

            //	If the unit is already allocated do nothing more.
            if (fileAllocations.get(fileName).contains(unitIndex))
                return;

            fileAllocations.get(fileName).add(unitIndex);

            //	Indicate that unit is no longer available for use by other files.
            if (freeUnitIndexes.contains(unitIndex))
                freeUnitIndexes.remove(unitIndex);

            //	Signal that total units have just gone up
            if (unitIndex > totalUnits)
                totalUnits = unitIndex;
        } finally {
            accessLock.writeLock().unlock();
        }
    }

    /**
     * Creates file with the given name if it does not exist
     *
     * @param fileName
     */
    final void _touch(String fileName) {
        try {
            accessLock.writeLock().lock();
            if (!fileAllocations.containsKey(fileName)) {
                fileAllocations.put(fileName, new ArrayList<Long>());
            }
        } finally {
            accessLock.writeLock().unlock();
        }
    }

    /**
     * Instruct the FAT to remove the given unit from use by the file (reclaiming it for other files to use
     *
     * @param fileName
     * @param unitIndex
     */
    final void _removeUnitFor(String fileName, long unitIndex) {
        if (!fileAllocations.containsKey(fileName))
            throw new MSSRuntime("Unexpected:  No file named '" + fileName + "' found on the system");

        if (!fileAllocations.get(fileName).remove(unitIndex)) {
            SystemLog.get().error("Unable to remove unit " + unitIndex + " from allocations for file '" + fileName + "'");

        }

        freeUnitIndexes.add(unitIndex);
    }

    /**
     * Does the given file exist?
     *
     * @param fileName
     * @return
     */
    final boolean _exists(String fileName) {
        try {
            accessLock.readLock().lock();
            return fileAllocations.containsKey(fileName);
        } finally {
            accessLock.readLock().unlock();
        }
    }

    /**
     * Get all units currently allocated to the given file
     *
     * @param fileName
     * @return
     */
    final List<Long> _unitsAllocated(String fileName) {
        try {
            accessLock.readLock().lock();
            if (!fileAllocations.containsKey(fileName))
                throw new MSSRuntime("Unexpected:  No file named '" + fileName + "' found on the system");

            List<Long> ret = new ArrayList<>();
            ret.addAll(fileAllocations.get(fileName));
            return ret;

        } finally {
            accessLock.readLock().unlock();
        }
    }

    /**
     * Get the next available index for allocating data for a file.  This method will NOT affect the FAT so
     * calling it repeatedly will simply return the currently available unit for allocation
     *
     * @return
     */
    final long nextAvailableUnitIndex() {
        try {
            accessLock.readLock().lock();

            if (CollectionUtils.isEmpty(freeUnitIndexes))
                return totalUnits + 1;

            else {
                return freeUnitIndexes.get(0);
            }

        } finally {
            accessLock.readLock().unlock();
        }
    }

    /**
     * List all files in this FAT table
     *
     * @return
     */
    final List<String> listFiles() {
        accessLock.readLock().lock();
        try {
            return fileAllocations.entrySet().stream().filter(e -> !FILENAME.equals(e.getKey())).map(Map.Entry::getKey).collect(Collectors.toList());
        } finally {
            accessLock.readLock().unlock();
        }
    }

    /**
     * Acquire read/write access lock to the FAT
     */
    final void accessLock() {
        accessLock.writeLock().lock();
    }

    /**
     * Release the read/write access lock to the FAT
     */
    final void releaseAccessLock() {
        accessLock.writeLock().unlock();
    }

    /**
     * Delete operation
     *
     * @param fileName
     */
    final void _delete(String fileName) {
        if (!fileAllocations.containsKey(fileName))
            throw new MSSRuntime("Unexpected:  No such file as '" + fileName + "' exists on the system!");

        //	Signal that all units from this file are available again
        List<Long> allocatedUnits = fileAllocations.remove(fileName);
        for (Long allocated : allocatedUnits) {
            freeUnitIndexes.add(allocated);
        }

        fileMetadata.remove(fileName);
    }

    /**
     * Gets the total units in this FAT
     *
     * @return
     */
    final long getTotalUnits() {
        return totalUnits;
    }

    @Override
    public final Object clone() throws CloneNotSupportedException {    //	NOSONAR
        throw new CloneNotSupportedException();
    }

    void _rename(String currentName, String newName) {
        try {
            accessLock.writeLock().lock();
            if (!fileAllocations.containsKey(currentName))
                throw new MSSRuntime("Unexpected:  No such file as '" + currentName + "' exists on the system!");

            List<Long> allocations = fileAllocations.remove(currentName);
            fileAllocations.put(newName, allocations);

            //  Transfer file details over to the new filename!
            FileDetails existingDetails = fileMetadata.remove(currentName);
            if(existingDetails != null){
                fileMetadata.put(newName, existingDetails);
            }

        } finally {
            accessLock.writeLock().unlock();
        }
    }

    /**
     * Close the FAT
     */
    void close() {
        //  Clear out an objects etc that should be wiped from mem..
    }

    FileDetails fileDetails(String fileName) {
        return fileMetadata.computeIfAbsent(fileName, (name) -> new FileDetails());
    }

    void setFileMeta(String fileName, FileMeta fileMeta) {
        FileDetails details = fileDetails(fileName);
        details.setFileMeta(fileMeta);
    }

    Optional<UnitShuffle> _nextShuffle() {
        if (!CollectionUtils.isEmpty(freeUnitIndexes) && !CollectionUtils.isEmpty(listFiles())) {
            List<Long> indexesAvailableToMoveChunkTo = new ArrayList<>(freeUnitIndexes);
            indexesAvailableToMoveChunkTo.sort((l1, l2) -> {
                if (l1 > l2) {
                    return 1;
                }
                if (l1 == l2) {
                    return 0;
                }
                return -1;
            });

            long destinationIndex = indexesAvailableToMoveChunkTo.get(indexesAvailableToMoveChunkTo.size()-1);
            Map<Long, String> unitAllocationsToFileNamesSortedByUnitIndex = getUnitNumbersToFileNamesSortedByUnitNumbers();
            long fromIndex = ((TreeMap<Long, String>) unitAllocationsToFileNamesSortedByUnitIndex).lastKey();

            UnitShuffle ret = new UnitShuffle(fromIndex, destinationIndex);
            List<Long> allocationSetToWhichFromBelongs = fileAllocations.get(unitAllocationsToFileNamesSortedByUnitIndex.get(fromIndex));
            if (allocationSetToWhichFromBelongs.get(0) != fromIndex) {                    //  If the from index is not the first chunk allocated
                int indexOfFrom = allocationSetToWhichFromBelongs.indexOf(fromIndex);   //  to the file then prepare to update its incoming reference
                ret.setIncomingReferenceUnit(allocationSetToWhichFromBelongs.get(indexOfFrom-1));
            }

            return Optional.of(ret);


        }
        return Optional.empty();
    }

    @NotNull
    private Map<Long, String> getUnitNumbersToFileNamesSortedByUnitNumbers() {
        Map<Long, String> unitAllocationsToFileNamesSortedByUnitIndex = new TreeMap<>();
        fileAllocations.entrySet().stream().forEach(stringListEntry ->
                stringListEntry.getValue().forEach(unit -> unitAllocationsToFileNamesSortedByUnitIndex.put(unit, stringListEntry.getKey())));
        return unitAllocationsToFileNamesSortedByUnitIndex;
    }

    long maxAllocatedIndex() {
        return ObjectUtils.defaultIfNull(((TreeMap<Long, String>)getUnitNumbersToFileNamesSortedByUnitNumbers()).lastKey(), 0L);
    }

    void updateUnitPlacement(long currentPosition, long newPosition) {

        List<Long> allocations;
        for (Map.Entry<String, List<Long>> entry : fileAllocations.entrySet()) {

            allocations = entry.getValue();

            if (allocations.contains(currentPosition)) {
                int index = allocations.indexOf(currentPosition);
                allocations.remove(currentPosition);
                allocations.add(index, newPosition);
                freeUnitIndexes.remove(newPosition);
                totalUnits--;   //  Drop total units since we just pulled a unit back
                break;
            }
        }

    }

    /**
     * Remove un-used units if they are beyond the max allocated unit
     */
    void trim() {

        long maxIndex;
        if(!CollectionUtils.isEmpty(listFiles())) {
            Map<Long, String> unitAllocationsToFileNamesSortedByUnitIndex = getUnitNumbersToFileNamesSortedByUnitNumbers();
            maxIndex = ((TreeMap<Long, String>) unitAllocationsToFileNamesSortedByUnitIndex).lastKey();
        } else {
            List<Long> unitsAllocatedToFAT = _unitsAllocated(FILENAME);
            maxIndex = unitsAllocatedToFAT.get(unitsAllocatedToFAT.size()-1);
        }

        for(Iterator<Long> freeUnitIterator = freeUnitIndexes.iterator(); freeUnitIterator.hasNext();){
            long nextFree = freeUnitIterator.next();
            if(nextFree > maxIndex){
                freeUnitIterator.remove();
            }
        }

        if(CollectionUtils.isEmpty(freeUnitIndexes)){   //  Update total units to reflect the total actually allocated
            this.totalUnits = maxIndex;
        }
    }

    List<Long> getFreeUnitIndexesForTest() {
        return Collections.unmodifiableList(this.freeUnitIndexes);
    }

    long _totalUnused() {
        return this.freeUnitIndexes.size();
    }
}
