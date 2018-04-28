package com.vandenbreemen.mobilesecurestorage.file;

import com.vandenbreemen.mobilesecurestorage.TestConstants;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.stream.IntStream;

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
    public void setup() throws ChunkedMediumException {
        sut = ChunkedFile.getChunkedFile(TestConstants.getTestFile("chunked_" + System.currentTimeMillis(), false));
    }

    @Test
    public void testWriteBytes() {
        sut.writeBytes(0, new byte[]{1, 2, 3, 4});

        byte[] read = sut.readBytes(0, 4);
        assertArrayEquals("Bytes expected",
                new byte[]{1, 2, 3, 4}, read);
    }

    @Test
    public void shouldWriteBytesAfterPrefix() throws Exception {
        sut.writeBytes(0, new byte[]{65, 65, 65, 65});
        sut.validateFile();
    }

    @Test
    public void testSeek() {
        sut.writeBytes(0, new byte[]{1, 2, 3, 4});
        byte[] read = sut.readBytes(1, 3);

        assertArrayEquals("Subset of bytes expected", new byte[]{2, 3, 4}, read);
    }

    @Test
    public void shouldAddByteMessage() throws Exception {
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
    public void shouldConsiderFileWithMessageAndNoChunksAsEmpty() throws Exception {
        sut.setMessage(new byte[]{
                'a',
                'b',
                'c',
                'd'
        });

        assertTrue("Empty chunked file", sut.isEmpty());
    }

    @Test
    public void shouldPreventMessageLongerThanMaxBytes() {
        int length = (ChunkedFile.PREFIX_BYTE_LEN - ChunkedFile.SIGNATURE.length) + 1;
        byte[] badMessage = new byte[length];
        IntStream.range(0, length).forEach(i -> badMessage[i] = 1);
        try {
            sut.setMessage(badMessage);
            fail("Max bytes reached");
        } catch (ChunkedMediumException cmx) {
            cmx.printStackTrace();
        }
    }

    @Test
    public void shouldAllowMessageAtMaxBytesLong() throws Exception {
        int length = (ChunkedFile.PREFIX_BYTE_LEN - ChunkedFile.SIGNATURE.length);
        byte[] messageBytes = new byte[length];
        IntStream.range(0, length).forEach(i -> messageBytes[i] = 1);

        sut.setMessage(messageBytes);

        assertArrayEquals("Prefix Bytes", messageBytes, sut.getMessage());
    }

    @Test
    public void shouldSupportAddingBytesAndMessageTogether() throws Exception {
        sut.setMessage(new byte[]{
                'a',
                'b',
                'c',
                'd'
        });

        sut.writeBytes(0, new byte[]{1, 2, 3, 4});

        byte[] read = sut.readBytes(0, 4);
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
    public void shouldSupportAddingBytesAndMessageTogetherOnExistingEmptyFile() throws Exception {

        sut = ChunkedFile.getChunkedFile(TestConstants.getTestFile("chunkedxt_" + System.currentTimeMillis()));

        sut.setMessage(new byte[]{
                'a',
                'b',
                'c',
                'd'
        });

        sut.writeBytes(0, new byte[]{1, 2, 3, 4});

        byte[] read = sut.readBytes(0, 4);
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
            ChunkedFile.getChunkedFile(testFile);
            fail("Invalid file format validation");
        } catch (ChunkedMediumException xmx) {
            xmx.printStackTrace();
        }
    }

    @Test
    public void shouldIdentifyFileAsEmpty() throws ChunkedMediumException {
        assertTrue("Empty", ChunkedFile.getChunkedFile(TestConstants.getTestFile("testEmpty", false)).isEmpty());
    }

    @Test
    public void shouldIdentifyChunkedFileCreatedUsingEmptyExistingFileAsEmpty() throws ChunkedMediumException {
        assertTrue("Empty", ChunkedFile.getChunkedFile(TestConstants.getTestFile("testEmpty")).isEmpty());
    }

    @Test
    public void shouldIdentifyFileWithContentAsNonEmpty() throws ChunkedMediumException {

        sut = ChunkedFile.getChunkedFile(TestConstants.getTestFile("testNonEmpty", false));

        sut.writeBytes(0, new byte[]{'k', 'e', 'v', 'i', 'n'});
        assertFalse("Non-empty", sut.isEmpty());
    }

}
