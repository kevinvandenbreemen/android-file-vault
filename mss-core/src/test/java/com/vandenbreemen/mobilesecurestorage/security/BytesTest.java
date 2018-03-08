package com.vandenbreemen.mobilesecurestorage.security;

import org.junit.Test;

import static org.junit.Assert.assertNotEquals;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class BytesTest {

    @Test
    public void testWipeBytes() {
        byte[] secret = new byte[]{1, 2, 3, 4};
        Bytes.wipe(secret);

        assertNotEquals(1, secret[0]);
        assertNotEquals(2, secret[1]);
        assertNotEquals(3, secret[2]);
        assertNotEquals(4, secret[3]);
    }

    @Test
    public void testRandomWipeBytes() {
        byte[] secret = new byte[]{1, 2, 3, 4};
        Bytes.wipeRandom(secret);

        assertNotEquals(1, secret[0]);
        assertNotEquals(2, secret[1]);
        assertNotEquals(3, secret[2]);
        assertNotEquals(4, secret[3]);
    }

}
