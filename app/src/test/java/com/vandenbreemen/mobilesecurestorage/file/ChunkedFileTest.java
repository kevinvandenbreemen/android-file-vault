package com.vandenbreemen.mobilesecurestorage.file;

import com.vandenbreemen.mobilesecurestorage.TestConstants;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class ChunkedFileTest {

    /**
     * System under test
     */
    private ChunkedFile sut;

    @Before
    public void setup() throws Exception {
        sut = new ChunkedFile(TestConstants.getTestFile("chunked_" + System.currentTimeMillis()));
    }

    @Test
    public void testWriteBytes() {
        sut.setCursor(0);
        sut.writeBytes(new byte[]{1, 2, 3, 4});

        byte[] read = sut.readBytes(4);
        assertArrayEquals("Bytes expected",
                new byte[]{1, 2, 3, 4}, read);
    }

    @Test
    public void testSeek() {
        sut.setCursor(0);
        sut.writeBytes(new byte[]{1, 2, 3, 4});

        sut.setCursor(1);
        byte[] read = sut.readBytes(3);

        assertArrayEquals("Subset of bytes expected", new byte[]{2, 3, 4}, read);
    }

}
