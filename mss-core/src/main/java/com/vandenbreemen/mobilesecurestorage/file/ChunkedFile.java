package com.vandenbreemen.mobilesecurestorage.file;

import com.vandenbreemen.mobilesecurestorage.data.ByteReader;
import com.vandenbreemen.mobilesecurestorage.log.SystemLog;
import com.vandenbreemen.mobilesecurestorage.message.MSSRuntime;

import org.spongycastle.pqc.math.linearalgebra.ByteUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import static com.vandenbreemen.mobilesecurestorage.data.ControlBytes.END_OF_HEADER;

/**
 * <h2>Intro</h2>
 * <p>Stores/reads "chunks" of bytes to and from a file
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class ChunkedFile {

    /**
     *
     */
    static final int PREFIX_BYTE_LEN = 256;

    static final byte[] SIGNATURE = {
            'N', 'E', 'S', 26, 'J', 'K'    //  NES Rom (Just Kidding)
    };

    /**
     * Location of the actual file
     */
    private File location;

    /**
     * Create a new {@link ChunkedFile} at the given location.  Note that the cursor will be set to the first byte of the file.
     *
     * @param location
     */
    public ChunkedFile(File location) {
        if (location == null)
            throw new MSSRuntime("Unexpected:  Null file ref");

        this.location = location;

        if (!this.location.exists()) {
            try {
                if (!this.location.createNewFile())
                    throw new MSSRuntime("Unable to create file at '" + this.location.getAbsolutePath() + "'");
                else {
                    SystemLog.get().info("Successfully created file '" + this.location.getAbsolutePath() + "'");

                }
            } catch (IOException ioex) {
                SystemLog.get().error("Failed to create file", ioex);
                throw new MSSRuntime("Unable to create file at '" + this.location.getAbsolutePath() + "'", ioex);
            }
        }


    }

    /**
     * Create a new chunked file with empty message
     *
     * @param location
     * @return
     */
    public static ChunkedFile getChunkedFile(File location) throws ChunkedMediumException {
        boolean exists = location.exists() && location.length() > 0;
        ChunkedFile ret = new ChunkedFile(location);
        if (!exists) {
            ret.addFileTypeSignature();
            try {
                ret.setMessage(new byte[0]);
            } catch (ChunkedMediumException stupidErrorHandling) {
                SystemLog.get().error("Failed to set empty message", stupidErrorHandling);
                throw new MSSRuntime("Something is screwed up - cannot sete empty message");
            }
        } else {
            ret.validateFile();
        }
        return ret;
    }

    void addFileTypeSignature() {
        writeBytesInternal(0, SIGNATURE);
    }

    /**
     * Check that this file is a valid chunked file.
     *
     * @throws ChunkedMediumException
     */
    void validateFile() throws ChunkedMediumException {
        byte[] sigBytes = readBytesInternal(0, SIGNATURE.length);
        if (!ByteUtils.equals(SIGNATURE, sigBytes)) {
            SystemLog.get().debug("ERROR VALIDATING FILE:  PREFIX IS:  " + new String(sigBytes) + " but expected " + new String(SIGNATURE));
            throw new ChunkedMediumException("Not a valid chunked file");
        }
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
     * Read the given number of bytes from the file at the given cursor (location in file)
     *
     *
     * @param cursor
     * @param numBytes
     * @return
     */
    public byte[] readBytes(long cursor, int numBytes) {
        return readBytesInternal(cursor + PREFIX_BYTE_LEN, numBytes);
    }

    private byte[] readBytesInternal(long cursor, int numBytes) {
        try (RandomAccessFile raf = get(true)) {
            raf.seek(cursor);
            byte[] buffer = new byte[numBytes];
            raf.read(buffer);
            return buffer;
        } catch (Exception ex) {
            throw new MSSRuntime("Error reading bytes", ex);
        }
    }

    void updateLength(long numBytes) {
        try (RandomAccessFile raf = new RandomAccessFile(location, "rws")) {
            raf.setLength(PREFIX_BYTE_LEN + numBytes);
        } catch (Exception ex) {
            throw new MSSRuntime("Failed to update length");
        }
    }

    /**
     * Write the given bytes to the file
     *
     *
     * @param cursor
     * @param bytes
     * @return
     */
    public ChunkedFile writeBytes(long cursor, byte[] bytes) {
        writeBytesInternal(cursor + PREFIX_BYTE_LEN, bytes);
        return this;
    }

    private void writeBytesInternal(long cursor, byte[] bytes) {
        try (RandomAccessFile raf = get(false)) {
            raf.seek(cursor);
            raf.write(bytes);
        } catch (Exception ex) {
            throw new MSSRuntime("Unexpected error writing data", ex);
        }
    }

    public byte[] getMessage() {
        byte[] raw = readBytesInternal(SIGNATURE.length, PREFIX_BYTE_LEN - SIGNATURE.length);
        return new ByteReader().readBytes(raw);
    }

    public final void setMessage(byte[] prefixBytes) throws ChunkedMediumException {

        if (prefixBytes.length > (PREFIX_BYTE_LEN - SIGNATURE.length)) {
            throw new ChunkedMediumException("Prefix byte longer than maximum allowed length of " + (PREFIX_BYTE_LEN - SIGNATURE.length));
        }

        byte[] msgBytes = new byte[PREFIX_BYTE_LEN - SIGNATURE.length];
        System.arraycopy(prefixBytes, 0, msgBytes, 0, prefixBytes.length);

        if (prefixBytes.length < (PREFIX_BYTE_LEN - SIGNATURE.length)) {
            msgBytes[prefixBytes.length] = END_OF_HEADER;
        }

        writeBytesInternal(SIGNATURE.length, msgBytes);
    }

    public boolean isEmpty() {
        return location.length() == PREFIX_BYTE_LEN;
    }
}
