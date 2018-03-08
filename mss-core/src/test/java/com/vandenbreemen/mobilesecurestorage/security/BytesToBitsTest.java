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
public class BytesToBitsTest {
    @Test
    public void sanityTestZeroPad() {
        BytesToBits b2b = new BytesToBits();
        byte[] bytes = new byte[]{1, 2, 3, 4};

        byte[] padded = b2b.zeroPad(bytes, 8);
        assertTrue("Padded should now be 0-padded and 8 bytes long",
                ByteUtils.equals(padded, new byte[]{1, 2, 3, 4, 0, 0, 0, 0})
        );
    }

    @Test
    public void testZeroPadLength() {
        BytesToBits b2b = new BytesToBits();
        byte[] bytes = new byte[]{1, 2, 3, 4};

        byte[] padded = b2b.zeroPad(bytes, 8);
        assertEquals("Padded should now be 0-padded and 8 bytes long",
                8, padded.length);
    }
}
