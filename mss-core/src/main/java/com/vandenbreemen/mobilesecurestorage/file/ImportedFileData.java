package com.vandenbreemen.mobilesecurestorage.file;

import java.io.Serializable;

/**
 * <h2>Intro</h2>
 * <p>Raw data imported from a file outside of the vault.
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class ImportedFileData implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1822306847715808049L;

    /**
     * Date from the file
     */
    private byte[] fileData;

    /**
     * Create an imported file data with a known set of bytes.
     *
     * @param fileData
     */
    public ImportedFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    /**
     * Data in the file
     *
     * @return
     */
    public byte[] getFileData() {
        return fileData;
    }
}
