package com.vandenbreemen.mobilesecurestorage.file;

import com.vandenbreemen.mobilesecurestorage.log.SystemLog;
import com.vandenbreemen.mobilesecurestorage.message.MSSRuntime;

import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
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

    /**
     *
     */
    private static final long serialVersionUID = 7715454070438785854L;

    /**
     * Special reserved filename
     */
    public static final String FILENAME = "____FAT%MMVDIRECTION_JIBBER";

    /**
     * Access lockdown
     */
    private transient ReentrantLock accessLock;

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

    public FAT() {
        this.accessLock = new ReentrantLock();
        this.freeUnitIndexes = new LinkedList<>();
        this.fileAllocations = new HashMap<>();
        this.totalUnits = 0;

        //	Unit allocation for the FAT
        fileAllocations.put(FILENAME, new ArrayList<Long>(Arrays.asList(0l /*First chunk of a file system is for FAT ALWAYS!*/)));
    }

    /**
     * Initialize FAT for use after recovery from a file
     */
    final void initialize() {
        this.accessLock = new ReentrantLock();
    }

    /**
     * Adds the given unit index to the given file.  If the unit is already allocated then this method will do nothing.
     *
     * @param fileName
     */
    final void _addUnitFor(String fileName, long unitIndex) {
        try {
            accessLock.lock();
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
            accessLock.unlock();
        }
    }

    /**
     * Creates file with the given name if it does not exist
     *
     * @param fileName
     */
    final void _touch(String fileName) {
        try {
            accessLock.lock();
            if (!fileAllocations.containsKey(fileName)) {
                fileAllocations.put(fileName, new ArrayList<Long>());
            }
        } finally {
            accessLock.unlock();
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
            accessLock.lock();
            return fileAllocations.containsKey(fileName);
        } finally {
            accessLock.unlock();
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
            accessLock.lock();
            if (!fileAllocations.containsKey(fileName))
                throw new MSSRuntime("Unexpected:  No file named '" + fileName + "' found on the system");

            List<Long> ret = new ArrayList<>();
            ret.addAll(fileAllocations.get(fileName));
            return ret;

        } finally {
            accessLock.unlock();
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
            accessLock.lock();

            if (CollectionUtils.isEmpty(freeUnitIndexes))
                return totalUnits + 1;

            else {
                return freeUnitIndexes.get(0);
            }

        } finally {
            accessLock.unlock();
        }
    }

    /**
     * List all files in this FAT table
     *
     * @return
     */
    final List<String> listFiles() {
        accessLock();
        try {
            return fileAllocations.entrySet().stream().filter(e -> !FILENAME.equals(e.getKey())).map(Map.Entry::getKey).collect(Collectors.toList());
        } finally {
            releaseAccessLock();
        }
    }

    /**
     * Acquire access lock to the FAT
     */
    final void accessLock() {
        accessLock.lock();
    }

    /**
     * Release the access lock to the FAT
     */
    final void releaseAccessLock() {
        accessLock.unlock();
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
        List<Long> allocatedUnits = fileAllocations.get(fileName);
        for (Long allocated : allocatedUnits) {
            freeUnitIndexes.add(allocated);
        }

        //	Finally remove the allocated file
        fileAllocations.remove(fileName);
    }

    /**
     * For testing only
     *
     * @return
     */
    final int numFreeAllocations() {
        return freeUnitIndexes.size();
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
            accessLock.lock();
            if (!fileAllocations.containsKey(currentName))
                throw new MSSRuntime("Unexpected:  No such file as '" + currentName + "' exists on the system!");

            List<Long> allocations = fileAllocations.remove(currentName);
            fileAllocations.put(newName, allocations);

        } finally {
            accessLock.unlock();
        }
    }

}
