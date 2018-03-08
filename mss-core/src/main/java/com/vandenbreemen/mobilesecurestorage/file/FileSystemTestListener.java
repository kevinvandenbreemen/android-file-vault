package com.vandenbreemen.mobilesecurestorage.file;

/**
 * <h2>Intro</h2>
 * <p>Listener for testing indexed file logic
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public interface FileSystemTestListener {
    /**
     * Log the start of a read of the file system
     */
    void logReadStart();

    /**
     * Log end of a read
     */
    void logReadEnd();

    /**
     * Log start of a write
     */
    void logWriteStart();

    /**
     * Log end of a write
     */
    void logWriteEnd();

    /**
     * Average time to read
     *
     * @return
     */
    long getAverageReadTime();

    /**
     * Average time to write data
     *
     * @return
     */
    long getAverageWriteTime();

    /**
     * Gets a printable report of average read/write times
     *
     * @return
     */
    String getAverageTimesReport();
}
