package com.vandenbreemen.mobilesecurestorage.security;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public interface BitGenerator {
    /**
     * For generating 0-padding
     */
    public static BitGenerator DEFAULT_0 = new BitGenerator() {

        @Override
        public int getBit(int position) {
            return 0;
        }
    };

    /**
     * Gets a new bit at the given position
     *
     * @param position
     * @return
     */
    public int getBit(int position);
}
