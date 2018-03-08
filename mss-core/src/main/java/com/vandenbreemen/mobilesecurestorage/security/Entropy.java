package com.vandenbreemen.mobilesecurestorage.security;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * <h2>Intro</h2>
 * <p>Class specializing in generating random noise
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class Entropy {
    /**
     * Zero byte for zeroing out data in memory
     */
    private static final byte ZERO = 0;
    private SecureRandom random;

    private Entropy() {
        this.random = new SecureRandom();
    }

    public static Entropy get() {
        return new Entropy();
    }

    /**
     * Next boolean
     *
     * @return
     */
    public boolean nextBoolean() {
        return random.nextBoolean();
    }

    /**
     * Populate the given byte array with zero byte
     *
     * @param bytes
     */
    public void fillBytes(byte[] bytes) {
        Arrays.fill(bytes, ZERO);
    }

    /**
     * Populate the given byte array with random data
     *
     * @param bytes
     */
    public void randomFillBytes(byte[] bytes) {
        random.nextBytes(bytes);
    }

    /**
     * Get next random integer
     *
     * @param maxInt
     * @return
     */
    public int nextInt(int maxInt) {
        return random.nextInt(maxInt);
    }
}
