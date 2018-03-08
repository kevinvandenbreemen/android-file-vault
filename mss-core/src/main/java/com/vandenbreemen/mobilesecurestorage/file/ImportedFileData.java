package com.vandenbreemen.mobilesecurestorage.file;

import com.vandenbreemen.mobilesecurestorage.log.SystemLog;
import com.vandenbreemen.mobilesecurestorage.message.MSSRuntime;
import com.vandenbreemen.mobilesecurestorage.security.Bytes;

import java.io.File;
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
     * Original location on disk of the data that were imported
     */
    private File originalLocation;

    /**
     * Date from the file
     */
    private byte[] fileData;

    /**
     * Create an imported file data with a known set of bytes.  It is recommended that you use
     * {@link #loadFileFromDisk(File)} instead of constructing this directly.
     *
     * @param originalLocation
     * @param fileData
     */
    public ImportedFileData(File originalLocation, byte[] fileData) {
        this.originalLocation = originalLocation;
        this.fileData = fileData;
    }

    /**
     * Load up all data in the given file, returning it as an {@link ImportedFileData}
     *
     * @param onDisk
     * @return
     */
    public static ImportedFileData loadFileFromDisk(File onDisk) {

        if (!onDisk.exists())
            throw new MSSRuntime("File '" + onDisk.getAbsolutePath() + "' does not exist");

        try {
            byte[] data = Bytes.loadBytesFromFile(onDisk);
            return new ImportedFileData(onDisk, data);
        } catch (Exception ex) {
            SystemLog.get().error("Error importing from file '" + onDisk.getAbsolutePath() + "'", ex);
            throw new MSSRuntime("Could not import file", ex);
        }
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
