package com.vandenbreemen.mobilesecurestorage.security.crypto.persistence;

import com.vandenbreemen.mobilesecurestorage.file.ChainedUnit;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class SecureDataUnit implements ChainedUnit {

    /**
     *
     */
    private static final long serialVersionUID = -4221711025953734966L;
    private byte[] data;

    /**
     * If this has a value then the data encoded using these units spans more than one chunk
     */
    private long locationOfNextUnit;

    /**
     * Optional key for next unit.  If this is present the {@link #locationOfNextUnit next unit} will be encrypted
     * using this key.
     */
    private byte[] keyForNextUnit;

    @Override
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * If this has a value then the data encoded using these units spans more than one chunk
     *
     * @return
     */
    public long getLocationOfNextUnit() {
        return locationOfNextUnit;
    }

    @Override
    public void setLocationOfNextUnit(long locationOfNextUnit) {
        this.locationOfNextUnit = locationOfNextUnit;
    }

    /**
     * Optional key for next unit.  If this is present the {@link #locationOfNextUnit next unit} will be encrypted
     * using this key.
     *
     * @return
     */
    public byte[] getKeyForNextUnit() {
        return keyForNextUnit;
    }

    /**
     * If there is a {@link #setLocationOfNextUnit(long) next unit in the stream for this object} then setting this will
     * cause that next unit to be encrypted using this key rather than default system-wide key
     *
     * @param keyForNextUnit
     */
    public SecureDataUnit setKeyForNextUnit(byte[] keyForNextUnit) {
        this.keyForNextUnit = keyForNextUnit;
        return this;
    }


}