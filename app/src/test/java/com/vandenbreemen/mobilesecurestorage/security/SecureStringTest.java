package com.vandenbreemen.mobilesecurestorage.security;

import org.junit.Test;
import org.spongycastle.pqc.math.linearalgebra.ByteUtils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class SecureStringTest {

    @Test
    public void testAddBytesToEmptySecureString() {
        SecureString empty = new SecureString();
        empty.addBytes(new byte[]{1, 2, 3, 4});

        assertTrue("Added bytes expected", ByteUtils.equals(new byte[]{1, 2, 3, 4}, empty.getBytes()));
    }

    @Test
    public void testGetSubsequence() {
        SecureString ss = new SecureString("This is a test".getBytes());
        String substring = ss.subSequence(0, 4).toString();
        assertEquals("End index should be exclusive", "This", substring);
    }


}
