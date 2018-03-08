package com.vandenbreemen.mobilesecurestorage.file;

import java.io.Serializable;

/**
 * <h2>Intro</h2>
 * <p>A unit of data that references another unit and contains data
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public interface ChainedUnit extends Serializable {
    /**
     * If this has a value then the data encoded using these units spans more than unit
     *
     * @return
     */
    public long getLocationOfNextUnit();

    /**
     * Indicate the location of the next unit of the data
     *
     * @param locationOfNextUnit
     * @return
     */
    public void setLocationOfNextUnit(long locationOfNextUnit);

    /**
     * Data stored in this data unit
     *
     * @return
     */
    public byte[] getData();

    /**
     * Set the data for this unit.
     *
     * @param data
     * @return
     */
    public void setData(byte[] data);
}
