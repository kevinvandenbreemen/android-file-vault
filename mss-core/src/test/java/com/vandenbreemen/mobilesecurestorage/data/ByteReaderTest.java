package com.vandenbreemen.mobilesecurestorage.data;

import org.junit.Test;

import static com.vandenbreemen.mobilesecurestorage.data.ControlBytes.END_OF_HEADER;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class ByteReaderTest {

    @Test
    public void shouldRecognizeEndOfHeader() {
        byte[] data = {
                1, 2, 3, 40, END_OF_HEADER, 0, 0
        };

        byte[] parsed = new ByteReader().readBytes(data);
        assertArrayEquals("Bytes", new byte[]{1, 2, 3, 40}, parsed);
    }

    @Test
    public void shouldCopyEntireBodyWhenNoEndOfHeader() {
        byte[] data = {
                1, 2, 3, 40
        };

        byte[] parsed = new ByteReader().readBytes(data);
        assertArrayEquals("Bytes", new byte[]{1, 2, 3, 40}, parsed);
    }

    @Test
    public void shouldRecognizeHeaderHasNoContent() {
        byte[] data = {
                END_OF_HEADER
        };

        byte[] parsed = new ByteReader().readBytes(data);

        assertEquals("Empty array", 0, parsed.length);

    }

}
