package com.vandenbreemen.mobilesecurestorage.file;

import com.vandenbreemen.mobilesecurestorage.TestConstants;
import com.vandenbreemen.mobilesecurestorage.data.ControlBytes;
import com.vandenbreemen.mobilesecurestorage.data.Serialization;
import com.vandenbreemen.mobilesecurestorage.file.IndexedFile.Chunk;
import com.vandenbreemen.mobilesecurestorage.security.Bytes;
import com.vandenbreemen.mobilesecurestorage.security.SecureString;
import com.vandenbreemen.mobilesecurestorage.security.crypto.DualLayerEncryptionService;
import com.vandenbreemen.mobilesecurestorage.security.crypto.EncryptionService;
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureDataUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.spongycastle.pqc.math.linearalgebra.ByteUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.iterableWithSize;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class IndexedFileTest {

    private SecureString password = DualLayerEncryptionService.generateKeys(new SecureString("Password123".getBytes()));

    @Test
    public void testEncodeChunk() throws Exception {
        byte[] toEncode = "this is a test".getBytes();
        byte[] encoded = new IndexedFile().encodeChunk(toEncode);
        assertEquals("Start of medium expected as first byte", ControlBytes.START_OF_MEDIUM, encoded[0]);
        assertEquals("length index expected as next byte", ControlBytes.LENGTH_IND, encoded[1]);


    }

    @Test
    public void testReadChunk() throws Exception {
        byte[] toEncode = "this is a test".getBytes();
        byte[] encoded = new IndexedFile().encodeChunk(toEncode);
        byte[] decoded = new IndexedFile().readChunk(encoded);

        byte[] expected = "this is a test".getBytes();
        assertEquals("Resultant chunk size not same as encoded", expected.length, decoded.length);

        for (int i = 0; i < decoded.length; i++) {
            assertEquals("Byte at index " + i + " is incorrect", expected[i], decoded[i]);
        }

    }

    @Test
    public void testWriteChunkToFile() throws Exception {

        File tempFile = TestConstants.getTestFile(("test_" + System.currentTimeMillis() + ".dat"));


        IndexedFile idf = new IndexedFile(tempFile);
        idf.writeBytes(1, "this is a test".getBytes());
        Chunk chunk = idf.readChunk(1);

        byte[] decoded = chunk.getBytes();
        byte[] expected = "this is a test".getBytes();
        assertEquals("Resultant chunk size not same as encoded", expected.length, decoded.length);

        for (int i = 0; i < decoded.length; i++) {
            assertEquals("Byte at index " + i + " is incorrect", expected[i], decoded[i]);
        }
    }

    @Test
    public void testTwoChunks() throws Exception {

        File tempFile = TestConstants.getTestFile(("test_1_" + System.currentTimeMillis() + ".dat"));


        IndexedFile idf = new IndexedFile(tempFile);
        idf.writeBytes(0, "this is a test".getBytes());

        idf.writeBytes(1, "Second chunk".getBytes());

        Chunk chunk = idf.readChunk(1);
        byte[] decoded = chunk.getBytes();
        byte[] expected = "Second chunk".getBytes();
        assertEquals("Resultant chunk size not same as encoded", expected.length, decoded.length);

        for (int i = 0; i < decoded.length; i++) {
            assertEquals("Byte at index " + i + " is incorrect", expected[i], decoded[i]);
        }

        //	Validate we can go back a chunk
        chunk = idf.readChunk(0);
        decoded = chunk.getBytes();
        expected = "this is a test".getBytes();
        assertEquals("Resultant chunk size not same as encoded", expected.length, decoded.length);

        for (int i = 0; i < decoded.length; i++) {
            assertEquals("Byte at index " + i + " is incorrect", expected[i], decoded[i]);
        }

    }

    @Test
    public void testMaxChunkSize() throws Exception {
        //	Density test.  Validate that the system can handle two totally full chunks
        File tempFile = TestConstants.getTestFile(("test_1_dense_" + System.currentTimeMillis() + ".dat"));

        IndexedFile idf = new IndexedFile(tempFile);

        byte[] firstChunk = new byte[idf.getMaxPayloadSize()];
        Arrays.fill(firstChunk, ControlBytes.NEW_PAGE);

        byte[] secondChunk = new byte[idf.getMaxPayloadSize()];
        Arrays.fill(secondChunk, ControlBytes.END_OF_MEDIUM);


        idf.writeBytes(0, firstChunk);
        idf.writeBytes(1, secondChunk);

        Chunk chunk = idf.readChunk(0);
        byte[] data = chunk.getBytes();
        assertEquals("Chunk size preservation expected", idf.getMaxPayloadSize(), data.length);
        for (byte b : data) {
            assertEquals("Byte should be control 'new page'", ControlBytes.NEW_PAGE, b);
        }

        chunk = idf.readChunk(1);
        data = chunk.getBytes();
        assertEquals("Chunk size preservation expected", idf.getMaxPayloadSize(), data.length);
        for (byte b : data) {
            assertEquals("Byte should be control 'new page'", ControlBytes.END_OF_MEDIUM, b);
        }
    }

    @Test
    public void testOverwriteAChunk() throws Exception {
        File tempFile = TestConstants.getTestFile(("test_1_" + System.currentTimeMillis() + ".dat"));


        IndexedFile idf = new IndexedFile(tempFile);
        idf.writeBytes(0, "this is a test".getBytes());

        idf.writeBytes(1, "Second chunk".getBytes());

        Chunk chunk = idf.readChunk(1);
        byte[] decoded = chunk.getBytes();
        byte[] expected = "Second chunk".getBytes();
        assertEquals("Resultant chunk size not same as encoded", expected.length, decoded.length);

        for (int i = 0; i < decoded.length; i++) {
            assertEquals("Byte at index " + i + " is incorrect", expected[i], decoded[i]);
        }

        //	Validate we can go back a chunk
        chunk = idf.readChunk(0);
        decoded = chunk.getBytes();
        expected = "this is a test".getBytes();
        assertEquals("Resultant chunk size not same as encoded", expected.length, decoded.length);

        for (int i = 0; i < decoded.length; i++) {
            assertEquals("Byte at index " + i + " is incorrect", expected[i], decoded[i]);
        }

        //	Now overwrite the first chunk!
        String newString = "Overwrite the first chunk with a totally new string";
        idf.writeBytes(0, newString.getBytes());
        chunk = idf.readChunk(0);
        decoded = chunk.getBytes();
        expected = newString.getBytes();
        assertEquals("Resultant chunk size not same as encoded", expected.length, decoded.length);

        for (int i = 0; i < decoded.length; i++) {
            assertEquals("Byte at index " + i + " is incorrect", expected[i], decoded[i]);
        }
    }

    @Test
    public void testStoreSerializedObjectChunk() throws Exception {
        File tempFile = TestConstants.getTestFile(("test_ser_" + System.currentTimeMillis() + ".dat"));


        ArrayList<String> secureList = new ArrayList<String>(Arrays.asList("Super", "Secret", "Stuff"));

        SecureDataUnit sda = new SecureDataUnit();
        sda.setData(Serialization.toBytes(secureList));

        IndexedFile idf = new IndexedFile(tempFile);
        idf.writeBytes(1, Serialization.toBytes(sda));
        IndexedFile.Chunk chunk = idf.readChunk(1);

        byte[] decoded = chunk.getBytes();

        sda = (SecureDataUnit) Serialization.deserializeBytes(decoded);
        secureList = (ArrayList<String>) Serialization.deserializeBytes(sda.getData());
        ;

        assertEquals("Possible corruption in serialized data!", "Secret", secureList.get(1));
    }

    private EncryptionService getEncryptionService() {
        return new DualLayerEncryptionService();
    }

    //	Ultimate test:  Can we encrypt a chunk of data?
    @Test
    public void testEncryptedChunk() throws Exception {

        File tempFile = TestConstants.getTestFile(("test_ser_enc" + System.currentTimeMillis() + ".dat"));


        ArrayList<String> strings = new ArrayList<String>(Arrays.asList("Larry", "Curly", "Moe"));
        SecureDataUnit sdu = new SecureDataUnit();
        sdu.setData(Serialization.toBytes(strings));

        byte[] encrypted = new DualLayerEncryptionService().encryptObject(password, sdu);


        IndexedFile idf = new IndexedFile(tempFile);
        idf.writeBytes(1, encrypted);
        IndexedFile.Chunk chunk = idf.readChunk(1);

        byte[] cipherText = chunk.getBytes();
        Object decrypted = new DualLayerEncryptionService().decryptObject(cipherText, password);
        assertNotNull("Decrypted object expected", decrypted);
        assertTrue("Decrypted secure data unit expected", decrypted instanceof SecureDataUnit);
        assertNotNull("Secure data unit contents expected", ((SecureDataUnit) decrypted).getData());
        ArrayList<String> recoveredList = (ArrayList<String>) Serialization.deserializeBytes(((SecureDataUnit) decrypted).getData());
        assertEquals("Possible data corruption", "Curly", recoveredList.get(1));
    }

    //	This is a smoke test.  No verification is performed
    @Test
    public void testSavingAFileOneUnit() throws Exception {

        File tempFile = TestConstants.getTestFile(("test_single_unit" + System.currentTimeMillis() + ".dat"));


        try {
            IndexedFile idf = new IndexedFile(tempFile);
            idf.storeObject("testfile", new ArrayList<String>(Arrays.asList("LARRY", "CURLY", "MOE")));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error");
        }
    }

    @Test
    public void testSavingAFileOneUnitAndThenRecovering() throws Exception {
        File tempFile = TestConstants.getTestFile(("test_rw_single_unit" + System.currentTimeMillis() + ".dat"));


        try {
            IndexedFile idf = new IndexedFile(tempFile);
            idf.storeObject("testfile", new ArrayList<String>(Arrays.asList("LARRY", "CURLY", "MOE")));

            idf = new IndexedFile(tempFile);
            List<String> files = idf.listFiles();

            assertEquals("Single file expected", 1, files.size());
            assertEquals("File name should be 'testfile'", "testfile", files.get(0));

            List<String> recovered = (List<String>)
                    idf.loadFile(files.get(0));

            assertNotNull("List expected", recovered);

            assertEquals("Possible data corruption", "CURLY", recovered.get(1));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error");
        }
    }


    @Test
    public void sanityTestMultiPartFileViewAddUnit() {

        File tempFile = TestConstants.getTestFile(("test_view_rw_multi_unit_add_" + System.currentTimeMillis() + ".dat"));

        try {
            IndexedFile idf = new IndexedFile(tempFile);
            idf.touch("test");


            IndexedFile.FileAllocationView view = idf.getFileView("test");

            assertNotNull("View expected", view);

            ChainedUnit newUnit = new ChainedDataUnit();
            ArrayList<String> testList = new ArrayList<String>(Arrays.asList("Larry", "Curly", "Moe"));
            newUnit.setData(Serialization.toBytes(testList));

            view.addUnit(newUnit);

            newUnit = new ChainedDataUnit();
            testList = new ArrayList<String>(Arrays.asList("Saphira", "LG482", "Starship"));
            newUnit.setData(Serialization.toBytes(testList));

            //	Force file re-load so we can throw more variables at the scenario!
            idf = new IndexedFile(tempFile);
            view = idf.getFileView("test");

            view.addUnit(newUnit);

            idf = new IndexedFile(tempFile);
            view = idf.getFileView("test");

            assertFalse("Units allocated expected", CollectionUtils.isEmpty(view.getUnits()));

            assertEquals("2 units should now be allocated to the file!", 2, view.getUnits().size());

            assertEquals("Units 1 and 2 should be allocated.  0 is reserved for FAT!", Long.valueOf(1), view.getUnits().get(0));
            assertEquals("Units 1 and 2 should be allocated.  0 is reserved for FAT!", Long.valueOf(2), view.getUnits().get(1));

            assertEquals("First unit should now point to next unit", 2l, view.readUnit(view.getUnits().get(0)).getLocationOfNextUnit());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected error");
        }

    }

    @Test
    public void testAllocateMultipleChunksBecauseObjectTooLarge() throws Exception {

        int maxItem = 10000;    //	Make a huge object with list with this many items

        File tempFile = TestConstants.getTestFile(("test_rw_multi_unit" + System.currentTimeMillis() + ".dat"));

        try {
            IndexedFile idf = new IndexedFile(tempFile);

            ArrayList<String> reallyLongList = new ArrayList<String>();

            List<String> expectedItems = new ArrayList<String>();

            for (int i = 0; i < maxItem; i++) {
                String str = "TEST_" + System.nanoTime();
                reallyLongList.add(str);
                expectedItems.add(str);
            }

            idf.storeObject("testfile", reallyLongList);

            idf = new IndexedFile(tempFile);
            List<String> files = idf.listFiles();

            assertEquals("Single file expected", 1, files.size());
            assertEquals("File name should be 'testfile'", "testfile", files.get(0));

            List<String> recovered = (List<String>)
                    idf.loadFile(files.get(0));

            assertEquals("Size of items not same", maxItem, recovered.size());

            for (int i = 0; i < maxItem; i++) {
                assertEquals("Possible data corruption", expectedItems.get(i), recovered.get(i));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error");
        }
    }


    //	Don't keep in main suite.  Just used to performance tune the FS
    //@Test
    public void performanceTestDataInsertion() {

        int maxItem = 10000;    //	Make a huge object with list with this many items

        File tempFile = TestConstants.getTestFile(("test_rw_multi_unit" + System.currentTimeMillis() + ".dat"));

        try {
            IndexedFile idf = new IndexedFile(tempFile);

            ArrayList<String> reallyLongList = new ArrayList<String>();

            List<String> expectedItems = new ArrayList<String>();

            for (int i = 0; i < maxItem; i++) {
                String str = "TEST_" + System.nanoTime();
                reallyLongList.add(str);
                expectedItems.add(str);
            }

            for (int i = 0; i < maxItem; i++) {
                idf.storeObject("testfile" + i, reallyLongList);
            }

            idf = new IndexedFile(tempFile);
            List<String> files = idf.listFiles();

            assertEquals("Single file expected", 1, files.size());
            assertEquals("File name should be 'testfile'", "testfile", files.get(0));

            List<String> recovered = (List<String>)
                    idf.loadFile(files.get(0));

            assertEquals("Size of items not same", maxItem, recovered.size());

            for (int i = 0; i < maxItem; i++) {
                assertEquals("Possible data corruption", expectedItems.get(i), recovered.get(i));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error");
        }
    }


    //	This test causes blocks from two files to criss-cross
    @Test
    public void testAllocateAccrossMultipleFiles() throws Exception {


        int maxItem = 10000;    //	Make a huge object with list with this many items

        File tempFile = TestConstants.getTestFile(("test_rw_multi_crisscross_unit" + System.currentTimeMillis() + ".dat"));


        String file1 = "file1";
        String file2 = "file2";

        try {
            IndexedFile idf = new IndexedFile(tempFile);

            ArrayList<String> list1 = new ArrayList<String>();
            ArrayList<String> list2 = new ArrayList<String>();

            for (int i = 0; i < 4; i++) {

                for (int j = 0; j < maxItem; j++) {
                    if (i % 2 == 0)
                        list1.add("LST1_" + System.nanoTime());
                    else
                        list2.add("LST2_" + System.nanoTime());
                }

                if (i % 2 == 0) {
                    idf.storeObject(file1, list1);
                } else
                    idf.storeObject(file2, list2);

            }

            //	Now try to load it all
            idf = new IndexedFile(tempFile);
            idf.testMode = true;
            assertEquals("There should be 2 files in the system", 2, idf.listFiles().size());

            List<String> rec1 = (List<String>) idf.loadFile(file1);
            List<String> rec2 = (List<String>) idf.loadFile(file2);

            assertEquals("2000 recs expected", 2 * maxItem, rec1.size());
            assertEquals("2000 recs expected", 2 * maxItem, rec2.size());

            for (String r1 : rec1) {
                assertTrue("POSSIBLE DATA CORRUPTION", r1.startsWith("LST1_"));
            }

            for (String r2 : rec2) {
                assertTrue("POSSIBLE DATA CORRUPTION", r2.startsWith("LST2_"));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error");
        }

    }


    @Test
    public void testAllocateAccrossMultipleFilesAndThenDeleteOne() throws Exception {


        int maxItem = 10000;    //	Make a huge object with list with this many items

        File tempFile = TestConstants.getTestFile(("test_rw_multi_crisscross_delete_unit" + System.currentTimeMillis() + ".dat"));


        String file1 = "file1";
        String file2 = "file2";

        AtomicInteger unitCounter = new AtomicInteger(1);

        try {
            IndexedFile idf = new IndexedFile(tempFile);

            ArrayList<String> list1 = new ArrayList<String>();
            ArrayList<String> list2 = new ArrayList<String>();

            for (int i = 0; i < 4; i++) {

                for (int j = 0; j < maxItem; j++) {
                    if (i % 2 == 0)
                        list1.add("LST1_" + System.nanoTime() + "_UNIT_" + unitCounter.getAndIncrement());
                    else
                        list2.add("LST2_" + System.nanoTime() + "_UNIT_" + unitCounter.getAndIncrement());
                }

                if (i % 2 == 0) {
                    idf.storeObject(file1, list1);
                } else
                    idf.storeObject(file2, list2);

            }

            assertEquals("Expected length  vs algorithm", 20000, list2.size());

            List<String> forCheck = (List<String>) idf.loadFile(file2);
            assertEquals("Length of stored", 20000, forCheck.size());

            //	Now try to load it all
            idf = new IndexedFile(tempFile);
            idf.testMode = true;

            assertEquals("There should be 2 files in the system", 2, idf.listFiles().size());
            assertEquals("No units should be available on the file (system should have to create new ones)", 0, idf.fat.numFreeAllocations());

            //	Delete the first file
            idf.deleteFile(file1);

            assertTrue("Should be units available on the file system after deletion!", idf.fat.numFreeAllocations() > 0);
            assertEquals("Only 1 file expected now", 1, idf.listFiles().size());

            //	
            idf = new IndexedFile(tempFile);
            idf.testMode = true;
            assertEquals("Only 1 file expected now", 1, idf.listFiles().size());
            assertTrue("Should be units available on the file system after deletion!", idf.fat.numFreeAllocations() > 0);

            //	Try and overwrite existing blocks with another full 20k items
            ArrayList<String> toUpdate = (ArrayList<String>) idf.loadFile(file2);
            for (int i = 0; i < maxItem * 2; i++) {
                toUpdate.add("LST2_" + System.nanoTime() + "_UNIT_" + unitCounter.getAndIncrement());
            }

            idf.storeObject(file2, toUpdate);
            idf = new IndexedFile(tempFile);
            idf.testMode = true;
            assertEquals("Only 1 file expected now", 1, idf.listFiles().size());

            List<String> rec2 = (List<String>) idf.loadFile(file2);
            assertEquals("40000 recs expected", 4 * maxItem, rec2.size());

            int lastUnit = -1;
            for (String r2 : rec2) {
                assertTrue("POSSIBLE DATA CORRUPTION", r2.startsWith("LST2_"));
                String unt = r2.split("_UNIT_")[1];
                int unit = Integer.parseInt(unt);
                if (lastUnit <= 0)
                    lastUnit = unit;
                else {
                    assertTrue("Data order not preserved!", unit > lastUnit);
                    lastUnit = unit;
                }
            }

            assertEquals("Next available index should be 11 since all units used", 3, idf.fat.nextAvailableUnitIndex());

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error");
        }

    }




    //	Simulate reducing a file in size.  The medium should in turn reclaim previous units for storage
    @Test
    public void testAllocateAccrossMultipleFilesAndThenTruncateOne() throws Exception {

        FileSystemTestListener listener = new DefaultFileSystemTestListener();


        int maxItem = 10000;    //	Make a huge object with list with this many items

        File tempFile = TestConstants.getTestFile(("test_rw_multi_crisscross_truncate_unit" + System.currentTimeMillis() + ".dat"));


        String file1 = "file1";
        String file2 = "file2";

        AtomicInteger unitCounter = new AtomicInteger(1);

        try {
            IndexedFile idf = new IndexedFile(tempFile);

            ArrayList<String> list1 = new ArrayList<String>();
            ArrayList<String> list2 = new ArrayList<String>();

            for (int i = 0; i < 4; i++) {

                for (int j = 0; j < maxItem; j++) {
                    if (i % 2 == 0)
                        list1.add("LST1_" + System.nanoTime() + "_UNIT_" + unitCounter.getAndIncrement());
                    else
                        list2.add("LST2_" + System.nanoTime() + "_UNIT_" + unitCounter.getAndIncrement());
                }

                if (i % 2 == 0) {
                    idf.storeObject(file1, list1);
                } else
                    idf.storeObject(file2, list2);

            }

            //	Now try to load it all
            idf = new IndexedFile(tempFile);
            idf.testMode = true;
            assertEquals("There should be 2 files in the system", 2, idf.listFiles().size());

            assertEquals("No units should be available on the file (system should have to create new ones)", 0, idf.fat.numFreeAllocations());

            //	Shorten/truncate the first file
            idf.storeObject(file1, new ArrayList<String>());    //	Totally empty list this time

            assertTrue("Should be units available on the file system after truncation!", idf.fat.numFreeAllocations() > 0);

            assertEquals(" 2 files still expected ", 2, idf.listFiles().size());

            //	
            idf = new IndexedFile(tempFile);
            idf.testMode = true;
            assertEquals(" 2 files still expected ", 2, idf.listFiles().size());

            assertTrue("Should be units available on the file system after deletion!", idf.fat.numFreeAllocations() > 0);

            //	Try and overwrite existing blocks with another full 20k items
            ArrayList<String> toUpdate = (ArrayList<String>) idf.loadFile(file2);
            for (int i = 0; i < maxItem * 2; i++) {
                toUpdate.add("LST2_" + System.nanoTime() + "_UNIT_" + unitCounter.getAndIncrement());
            }

            idf.storeObject(file2, toUpdate);
            idf = new IndexedFile(tempFile);
            idf.testMode = true;
            assertEquals(" 2 files still expected ", 2, idf.listFiles().size());

            List<String> rec2 = (List<String>) idf.loadFile(file2);
            assertEquals("40000 recs expected", 4 * maxItem, rec2.size());

            int lastUnit = -1;
            for (String r2 : rec2) {
                assertTrue("POSSIBLE DATA CORRUPTION", r2.startsWith("LST2_"));
                String unt = r2.split("_UNIT_")[1];
                int unit = Integer.parseInt(unt);
                if (lastUnit <= 0)
                    lastUnit = unit;
                else {
                    assertTrue("Data order not preserved!", unit > lastUnit);
                    lastUnit = unit;
                }
            }

            assertEquals("Next available index should be 11 since all units used", 5, idf.fat.nextAvailableUnitIndex());


        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error");
        }

    }

    //	System must support deleting more than one filre
    @Test
    public void testDeleteMultipleFiles() throws Exception {
        File tempFile = TestConstants.getTestFile("test_jpgimport" + System.currentTimeMillis() + ".dat");
        tempFile.deleteOnExit();
        tempFile.deleteOnExit();

        IndexedFile idf = new IndexedFile(tempFile);
        idf.importFile(TestConstants.TEST_RES_IMG_1);
        idf.importFile(TestConstants.TEST_RES_IMG_2);
        idf.importFile(TestConstants.TEST_RES_IMG_3);

        idf.deleteFiles(TestConstants.TEST_RES_IMG_1.getName(), TestConstants.TEST_RES_IMG_2.getName());

        assertEquals("Single file remaining expected", 1, idf.listFiles().size());

    }

    @Test
    public void testDeleteMultipleFilesLoadOne() throws Exception {

        File tempFile = TestConstants.getTestFile("test_jpgimport" + System.currentTimeMillis() + ".dat");
        tempFile.deleteOnExit();
        tempFile.deleteOnExit();

        byte[] expectedBytes =
                Bytes.loadBytesFromFile(TestConstants.TEST_RES_IMG_3);

        IndexedFile idf = new IndexedFile(tempFile);
        idf.importFile(TestConstants.TEST_RES_IMG_1);
        idf.importFile(TestConstants.TEST_RES_IMG_2);
        idf.importFile(TestConstants.TEST_RES_IMG_3);

        idf.deleteFiles(TestConstants.TEST_RES_IMG_1.getName(), TestConstants.TEST_RES_IMG_2.getName());

        byte[] loaded = idf.loadBytesFromFile(TestConstants.TEST_RES_IMG_3.getName());
        for (int i = 0; i < expectedBytes.length; i++) {
            assertEquals("Data corruption at byte " + i, expectedBytes[i], loaded[i]);
        }

    }

    @Test
    public void testPreventDeleteSameFileTwice() throws Exception {
        File tempFile = TestConstants.getTestFile("test_jpgimport" + System.currentTimeMillis() + ".dat");
        tempFile.deleteOnExit();
        tempFile.deleteOnExit();

        IndexedFile idf = new IndexedFile(tempFile);
        idf.importFile(TestConstants.TEST_RES_IMG_1);
        idf.importFile(TestConstants.TEST_RES_IMG_2);
        idf.importFile(TestConstants.TEST_RES_IMG_3);

        try {
            idf.deleteFiles("1.jpg", "1.jpg");
            fail("System should not have allowed deleting same file twice");
        } catch (ChunkedMediumException cmx) {
            System.out.println(cmx.getMessage());
        }
    }

    //	Validate importing files from disk into an indexed file
    @Test
    public void testImportFile() throws Exception {

        FileSystemTestListener listener = new DefaultFileSystemTestListener();

        File tempFile = TestConstants.getTestFile("test_jpgimport" + System.currentTimeMillis() + ".dat");
        tempFile.deleteOnExit();

        try {

            byte[] expectedBytes =
                    Bytes.loadBytesFromFile(TestConstants.TEST_RES_IMG_1);

            IndexedFile idf = new IndexedFile(tempFile);
            assertEquals(
                    "System should return filename generated during import",
                    TestConstants.TEST_RES_IMG_1.getName(), idf.importFile(TestConstants.TEST_RES_IMG_1));

            idf = new IndexedFile(tempFile);
            assertEquals("Single file expected", 1, idf.listFiles().size());
            assertEquals("Imported file name should be same as name on disk", TestConstants.TEST_RES_IMG_1.getName(), idf.listFiles().get(0));

            Object imported = idf.loadFile(idf.listFiles().get(0));

            assertTrue("Imported file data object expected", imported instanceof ImportedFileData);

            ImportedFileData data = (ImportedFileData) imported;
            assertNotNull("Data expected", data.getFileData());

            assertEquals("Byte count mismatch", expectedBytes.length, data.getFileData().length);
            for (int i = 0; i < expectedBytes.length; i++) {
                assertEquals("Data corruption at byte " + i, expectedBytes[i], data.getFileData()[i]);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected error importing");
        }

    }

    @Test
    public void testLoadObjectAcrossMultiChunks() throws Exception {
        int maxItem = 10000;    //	Make a huge object with list with this many items

        File tempFile = TestConstants.getTestFile(("test_rw_multi_crisscross_delete_unit" + System.currentTimeMillis() + ".dat"));
        String file2 = "file2";

        AtomicInteger unitCounter = new AtomicInteger(1);

        IndexedFile idf = new IndexedFile(tempFile);
        ArrayList<String> list2 = new ArrayList<String>();

        for (int i = 0; i < 2 * maxItem; i++) {
            list2.add("LST2_" + System.nanoTime() + "_UNIT_" + unitCounter.getAndIncrement());
        }

        assertEquals("Expected length  vs algorithm", 20000, list2.size());

        idf.storeObject(file2, list2);

        List<String> forCheck = (List<String>) idf.loadFile(file2);
        assertEquals("Length of stored", 20000, forCheck.size());
    }

    @Test
    public void testLoadObject() throws Exception {
        IndexedFile indexedFile = new IndexedFile(TestConstants.getTestFile("loadObject"));
        ArrayList<String> testStrings = new ArrayList<>(Arrays.asList("Larry", "Curly", "Moe"));

        indexedFile.storeObject("test", testStrings);

        indexedFile = new IndexedFile(TestConstants.getTestFile("loadObject"));
        testStrings = (ArrayList<String>) indexedFile.loadFile("test");

        assertThat(testStrings, allOf(
                iterableWithSize(3),
                hasItem("Larry"),
                hasItem("Curly"),
                hasItem("Moe")
        ));
    }

    @Test
    public void testRenameFile() throws Exception {
        IndexedFile indexedFile = new IndexedFile(TestConstants.getTestFile("loadObject"));
        ArrayList<String> testStrings = new ArrayList<>(Arrays.asList("Larry", "Curly", "Moe"));

        indexedFile.storeObject("test", testStrings);
        indexedFile.rename("test", "newName");

        indexedFile = new IndexedFile(TestConstants.getTestFile("loadObject"));

        List<String> files = indexedFile.listFiles();
        assertThat(files, allOf(
                iterableWithSize(1),
                hasItem("newName")
        ));

        testStrings = (ArrayList<String>) indexedFile.loadFile("newName");

        assertThat(testStrings, allOf(
                iterableWithSize(3),
                hasItem("Larry"),
                hasItem("Curly"),
                hasItem("Moe")
        ));
    }

    @Test
    public void testUpdateObject() throws Exception {
        IndexedFile indexedFile = new IndexedFile(TestConstants.getTestFile("updateObject"));
        ArrayList<String> testStrings = new ArrayList<>(Arrays.asList("Larry", "Curly", "Moe"));

        indexedFile.storeObject("test", testStrings);

        indexedFile = new IndexedFile(TestConstants.getTestFile("updateObject"));
        testStrings = (ArrayList<String>) indexedFile.loadFile("test");
        testStrings.add("Dragon");

        indexedFile.storeObject("test", testStrings);

        indexedFile = new IndexedFile(TestConstants.getTestFile("updateObject"));
        testStrings = (ArrayList<String>) indexedFile.loadFile("test");

        assertThat(testStrings, allOf(
                iterableWithSize(4),
                hasItem("Larry"),
                hasItem("Curly"),
                hasItem("Moe"),
                hasItem("Dragon")
        ));
    }

    @Test
    public void shouldCacheFileData() throws Exception {
        IndexedFile indexedFile = new IndexedFile(TestConstants.getTestFile("cacheObject"));
        ArrayList<String> testStrings = new ArrayList<>(Arrays.asList("Larry", "Curly", "Moe"));

        indexedFile.storeObject("test", testStrings);

        indexedFile = new IndexedFile(TestConstants.getTestFile("cacheObject"));
        ArrayList<String> loadedTestStrings = (ArrayList<String>) indexedFile.loadAndCacheFile("test");

        assertEquals("Loaded test strings", testStrings, loadedTestStrings);
        loadedTestStrings = (ArrayList<String>) indexedFile.loadAndCacheFile("test");
        assertEquals("Loaded test strings", testStrings, loadedTestStrings);

    }

    @Test
    public void shouldUpdateCachedFileData() throws Exception {
        IndexedFile indexedFile = new IndexedFile(TestConstants.getTestFile("cacheObject"));
        ArrayList<String> testStrings = new ArrayList<>(Arrays.asList("Larry", "Curly", "Moe"));

        indexedFile.storeObject("test", testStrings);

        indexedFile = new IndexedFile(TestConstants.getTestFile("cacheObject"));
        ArrayList<String> loadedTestStrings = (ArrayList<String>) indexedFile.loadAndCacheFile("test");

        loadedTestStrings.add("Dragon");
        indexedFile.storeObject("test", loadedTestStrings);

        ArrayList<String> expected = new ArrayList<>(Arrays.asList("Larry", "Curly", "Moe", "Dragon"));

        loadedTestStrings = (ArrayList<String>) indexedFile.loadAndCacheFile("test");
        assertEquals("Updated", expected, loadedTestStrings);
    }

    @Test
    public void testUpdateDataUnit() throws Exception {
        IndexedFile indexedFile = new IndexedFile(TestConstants.getTestFile("updateUnit"));
        ChainedDataUnit cdu = new ChainedDataUnit();
        cdu.setData(new byte[]{1, 2, 3, 4});

        indexedFile.touch("test");
        indexedFile.addDataUnit("test", cdu);

        Long unitIdx = indexedFile.fat._unitsAllocated("test").get(0);
        cdu = new ChainedDataUnit();
        cdu.setData(new byte[]{2, 5, 0, 1});
        indexedFile.updateDataUnit("test", unitIdx, cdu);

        byte[] data = indexedFile.getFileView("test").readUnit(unitIdx).getData();


        assertTrue("Updated unit expected", ByteUtils.equals(new byte[]{2, 5, 0, 1}, data));
    }

    @Test
    public void testUpdateDataUnitWithUpdater() throws Exception {
        IndexedFile indexedFile = new IndexedFile(TestConstants.getTestFile("updateUnit"));
        ChainedDataUnit cdu = new ChainedDataUnit();
        cdu.setData(new byte[]{1, 2, 3, 4});

        indexedFile.touch("test");
        indexedFile.addDataUnit("test", cdu);

        Long unitIdx = indexedFile.fat._unitsAllocated("test").get(0);
        cdu = new ChainedDataUnit();
        cdu.setData(new byte[]{2, 5, 0, 1});
        indexedFile.getFileView("test").getUnitUpdater(unitIdx).update(cdu);

        byte[] data = indexedFile.getFileView("test").readUnit(unitIdx).getData();


        assertTrue("Updated unit expected", ByteUtils.equals(new byte[]{2, 5, 0, 1}, data));
    }

}
