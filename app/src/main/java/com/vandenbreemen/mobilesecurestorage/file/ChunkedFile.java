package com.vandenbreemen.mobilesecurestorage.file;

import com.vandenbreemen.mobilesecurestorage.log.SystemLog;
import com.vandenbreemen.mobilesecurestorage.message.MSSRuntime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * <h2>Intro</h2>
 * <p>Stores/reads "chunks" of bytes to and from a file
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class ChunkedFile {


    /**
     * Location of the actual file
     */
    private File location;

    /**
     * Where in the file we are
     */
    private long cursor;

    /**
     * Create a new {@link ChunkedFile} at the given location.  Note that the cursor will be set to the first byte of the file.  If you want
     * to append to the file please call {@link #moveToEnd()}.
     *
     * @param location
     */
    public ChunkedFile(File location) throws Exception {
        if (location == null)
            throw new MSSRuntime("Unexpected:  Null file ref");
        if (!location.exists()) {
            if (!location.createNewFile())
                throw new MSSRuntime("Unable to create file at '" + location.getAbsolutePath() + "'");
            else
                SystemLog.get().info("Successfully created file '" + location.getAbsolutePath() + "'");
        }
        this.location = location;
        this.cursor = 0;
    }

    /**
     * Obtains {@link RandomAccessFile} for manipulating the file itself
     *
     * @param readOnly
     * @return
     */
    private RandomAccessFile get(boolean readOnly) {
        try {
            return new RandomAccessFile(location, readOnly ? "r" : "rw");
        } catch (FileNotFoundException wontHappen) {
            throw new MSSRuntime("Unexpected:  File '" + location.getAbsolutePath() + "' suddenly doesn't exist!");
        }
    }

    /**
     * Sets the location of the cursor
     *
     * @param location
     * @return
     */
    public ChunkedFile setCursor(long location) throws IllegalArgumentException {
        try (RandomAccessFile raf = get(true)) {
            raf.seek(location);
            cursor = location;
            return this;
        } catch (IOException ioe) {
            SystemLog.get().error("Unable to move to location " + location, ioe);
            throw new IllegalArgumentException("Location " + location + " is out of bounds");
        }
    }

    /**
     * Read the given number of bytes from the current {@link #cursor}.
     *
     * @param numBytes
     * @return
     */
    public byte[] readBytes(int numBytes) {
        try (RandomAccessFile raf = get(true)) {
            raf.seek(cursor);
            byte[] buffer = new byte[numBytes];
            raf.read(buffer);
            return buffer;
        } catch (Exception ex) {
            throw new MSSRuntime("Error reading bytes", ex);
        }
    }

    /**
     * Write the given bytes to the file
     *
     * @param bytes
     * @return
     */
    public ChunkedFile writeBytes(byte[] bytes) {
        try (RandomAccessFile raf = get(false)) {
            raf.seek(cursor);
            raf.write(bytes);
            return this;
        } catch (Exception ex) {
            throw new MSSRuntime("Unexpected error writing data", ex);
        }
    }

}
