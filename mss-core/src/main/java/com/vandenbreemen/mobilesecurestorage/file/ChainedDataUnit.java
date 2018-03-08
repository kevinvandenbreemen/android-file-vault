package com.vandenbreemen.mobilesecurestorage.file;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class ChainedDataUnit implements ChainedUnit {
    /**
     *
     */
    private static final long serialVersionUID = 4892024249957685228L;
    /**
     * If this has a value then the data encoded using these units spans more than one unit
     */
    private long locationOfNextUnit;

    /**
     * Raw data in this unit
     */
    private byte[] data;

    @Override
    public long getLocationOfNextUnit() {
        return locationOfNextUnit;
    }

    /**
     * Set location (chunk number) on the file system where the next data unit of the file
     * is to reside
     *
     * @param locationOfNextUnit
     * @return
     */
    @Override
    public void setLocationOfNextUnit(long locationOfNextUnit) {
        this.locationOfNextUnit = locationOfNextUnit;
    }

    public byte[] getData() {
        return data;
    }

    /**
     * Populate data for this unit
     *
     * @param data
     * @return
     */
    public void setData(byte[] data) {
        this.data = data;
    }
}
