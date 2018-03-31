package com.vandenbreemen.mobilesecurestorage.file;

import com.vandenbreemen.mobilesecurestorage.TestConstants;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.RandomAccessFile;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
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
    public void setup() {
        sut = ChunkedFile.getChunkedFile(TestConstants.getTestFile("chunked_" + System.currentTimeMillis(), false));
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
    public void shouldWriteBytesAfterPrefix() throws Exception {
        sut.setCursor(0);
        sut.writeBytes(new byte[]{65, 65, 65, 65});
        sut.validateFile();
    }

    @Test
    public void testSeek() {
        sut.setCursor(0);
        sut.writeBytes(new byte[]{1, 2, 3, 4});

        sut.setCursor(1);
        byte[] read = sut.readBytes(3);

        assertArrayEquals("Subset of bytes expected", new byte[]{2, 3, 4}, read);
    }

    @Test
    public void shouldAddByteMessage() {
        sut.setMessage(new byte[]{
                'a',
                'b',
                'c',
                'd'
        });

        assertArrayEquals("Prefix Bytes", new byte[]{
                'a',
                'b',
                'c',
                'd'
        }, sut.getMessage());
    }

    @Test
    public void shouldSupportAddingBytesAndMessageTogether() {
        sut.setMessage(new byte[]{
                'a',
                'b',
                'c',
                'd'
        });

        sut.setCursor(0);
        sut.writeBytes(new byte[]{1, 2, 3, 4});

        byte[] read = sut.readBytes(4);
        assertArrayEquals("Bytes expected",
                new byte[]{1, 2, 3, 4}, read);

        assertArrayEquals("Prefix Bytes", new byte[]{
                'a',
                'b',
                'c',
                'd'
        }, sut.getMessage());
    }

    @Test
    public void shouldSupportAddingBytesAndMessageTogetherOnExistingEmptyFile() {

        sut = ChunkedFile.getChunkedFile(TestConstants.getTestFile("chunkedxt_" + System.currentTimeMillis()));

        sut.setMessage(new byte[]{
                'a',
                'b',
                'c',
                'd'
        });

        sut.setCursor(0);
        sut.writeBytes(new byte[]{1, 2, 3, 4});

        byte[] read = sut.readBytes(4);
        assertArrayEquals("Bytes expected",
                new byte[]{1, 2, 3, 4}, read);

        assertArrayEquals("Prefix Bytes", new byte[]{
                'a',
                'b',
                'c',
                'd'
        }, sut.getMessage());
    }

    @Test
    public void shouldRecognizeInvalidFile() throws Exception {
        File testFile = TestConstants.getTestFile("fakeFile_" + System.currentTimeMillis());
        RandomAccessFile raf = new RandomAccessFile(testFile, "rw");
        raf.seek(0);
        raf.write(new byte[]{1, 2, 3});
        raf.close();

        try {
            ChunkedFile.getChunkedFile(testFile).validateFile();
            fail("Invalid file format validation");
        } catch (ChunkedMediumException xmx) {
            xmx.printStackTrace();
        }
    }

    @Test
    public void shouldIdentifyFileAsEmpty() {
        assertTrue("Empty", ChunkedFile.getChunkedFile(TestConstants.getTestFile("testEmpty", false)).isEmpty());
    }

    @Test
    public void shouldIdentifyChunkedFileCreatedUsingEmptyExistingFileAsEmpty() {
        assertTrue("Empty", ChunkedFile.getChunkedFile(TestConstants.getTestFile("testEmpty")).isEmpty());
    }

    @Test
    public void shouldIdentifyFileWithContentAsNonEmpty() {

        sut = ChunkedFile.getChunkedFile(TestConstants.getTestFile("testNonEmpty", false));

        sut.setCursor(0);
        sut.writeBytes(new byte[]{'k', 'e', 'v', 'i', 'n'});
        assertFalse("Non-empty", sut.isEmpty());
    }

}
