package com.vandenbreemen.mobilesecurestorage.security;

import com.vandenbreemen.mobilesecurestorage.log.SystemLog;
import com.vandenbreemen.mobilesecurestorage.message.MSSRuntime;

import java.io.File;
import java.io.FileInputStream;

/**
 * <h2>Intro</h2>
 * <p>Helper for working with raw bytes in memory
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class Bytes {

    private Bytes() {
    }

    /**
     * Wipe over the given array of bytes with random noise
     *
     * @param data
     */
    //	See also https://stackoverflow.com/a/12008111
    public static void wipe(byte[] data) {
        // wipe the array with 0's
        Entropy.get().fillBytes(data);
        // compute hash to force optimizer to do the wipe
        int hash = 0;
        for (int i = 0; i < data.length; i++) {
            hash = hash * 31 + (int) data[i];
        }
    }

    /**
     * Wipe the given byte array with random noise.  This is more CPU intensive but provides a bit
     * more security for sensitive information like encryption keys being removed from memory as it MAY
     * make it more difficult for physical analysis of the hardware to yield information
     *
     * @param data
     */
    public static void wipeRandom(byte[] data) {

        Entropy.get().randomFillBytes(data);

        int hash = 0;
        for (int i = 0; i < data.length; i++) {
            hash = hash * 31 + (int) data[i];
        }
    }

    /**
     * Load all the bytes in the given file.  If an error occurs this method will log it in
     * the system log and then throw a runtime error
     *
     * @param openedFile
     * @return
     */
    public static byte[] loadBytesFromFile(File openedFile) {
        try (FileInputStream fis = new FileInputStream(openedFile)) {
            byte[] bytesFromFile = new byte[(int) openedFile.length()];

            //	Load in the bytes from the file
            fis.read(bytesFromFile);
            return bytesFromFile;
        } catch (Exception ex) {
            SystemLog.get().error("Unable to load data from file", ex);
            throw new MSSRuntime("Unexpected error loading from file", ex);
        }
    }

}
