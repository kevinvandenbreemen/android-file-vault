package com.vandenbreemen.mobilesecurestorage.file;

import com.vandenbreemen.mobilesecurestorage.TestConstants;
import com.vandenbreemen.mobilesecurestorage.data.ControlBytes;
import com.vandenbreemen.mobilesecurestorage.data.Serialization;
import com.vandenbreemen.mobilesecurestorage.file.IndexedFile.Chunk;
import com.vandenbreemen.mobilesecurestorage.security.Bytes;
import com.vandenbreemen.mobilesecurestorage.security.Entropy;
import com.vandenbreemen.mobilesecurestorage.security.SecureString;
import com.vandenbreemen.mobilesecurestorage.security.crypto.DualLayerEncryptionService;
import com.vandenbreemen.mobilesecurestorage.security.crypto.EncryptionService;
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureDataUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;

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
    public void testEncodeChunk() {
        byte[] toEncode = "this is a test".getBytes();
        byte[] encoded = new IndexedFile().encodeChunk(toEncode, false);
        assertEquals("Start of medium expected as first byte", ControlBytes.START_OF_MEDIUM, encoded[0]);
        assertEquals("length index expected as next byte", ControlBytes.LENGTH_IND, encoded[1]);


    }

    @Test
    public void testReadChunk() {
        byte[] toEncode = "this is a test".getBytes();
        byte[] encoded = new IndexedFile().encodeChunk(toEncode, false);
        byte[] decoded = new IndexedFile().readChunk(encoded);

        byte[] expected = "this is a test".getBytes();
        assertEquals("Resultant chunk size not same as encoded", expected.length, decoded.length);

        for (int i = 0; i < decoded.length; i++) {
            assertEquals("Byte at index " + i + " is incorrect", expected[i], decoded[i]);
        }

    }

    @Test
    public void testWriteChunkToFile() {

        File tempFile = TestConstants.getTestFile(("test_" + System.currentTimeMillis() + ".dat"));


        IndexedFile idf = new IndexedFile(tempFile);
        idf.writeBytes("this is a test".getBytes(), true);
        Chunk chunk = idf.readChunk();

        byte[] decoded = chunk.getBytes();
        byte[] expected = "this is a test".getBytes();
        assertEquals("Resultant chunk size not same as encoded", expected.length, decoded.length);

        for (int i = 0; i < decoded.length; i++) {
            assertEquals("Byte at index " + i + " is incorrect", expected[i], decoded[i]);
        }
    }

    @Test
    public void testTwoChunks() {

        File tempFile = TestConstants.getTestFile(("test_1_" + System.currentTimeMillis() + ".dat"));


        IndexedFile idf = new IndexedFile(tempFile);
        idf.writeBytes("this is a test".getBytes(), true);

        idf.toIndex(1);
        idf.writeBytes("Second chunk".getBytes(), true);

        Chunk chunk = idf.readChunk();
        byte[] decoded = chunk.getBytes();
        byte[] expected = "Second chunk".getBytes();
        assertEquals("Resultant chunk size not same as encoded", expected.length, decoded.length);

        for (int i = 0; i < decoded.length; i++) {
            assertEquals("Byte at index " + i + " is incorrect", expected[i], decoded[i]);
        }

        //	Validate we can go back a chunk
        idf.toIndex(0);
        chunk = idf.readChunk();
        decoded = chunk.getBytes();
        expected = "this is a test".getBytes();
        assertEquals("Resultant chunk size not same as encoded", expected.length, decoded.length);

        for (int i = 0; i < decoded.length; i++) {
            assertEquals("Byte at index " + i + " is incorrect", expected[i], decoded[i]);
        }

    }

    @Test
    public void testMaxChunkSize() {
        //	Density test.  Validate that the system can handle two totally full chunks
        File tempFile = TestConstants.getTestFile(("test_1_dense_" + System.currentTimeMillis() + ".dat"));

        IndexedFile idf = new IndexedFile(tempFile);

        byte[] firstChunk = new byte[idf.getMaxPayloadSize()];
        Arrays.fill(firstChunk, ControlBytes.NEW_PAGE);

        byte[] secondChunk = new byte[idf.getMaxPayloadSize()];
        Arrays.fill(secondChunk, ControlBytes.END_OF_MEDIUM);


        idf.writeBytes(firstChunk, true);
        idf.toIndex(1);
        idf.writeBytes(secondChunk, true);

        idf.toIndex(0);
        Chunk chunk = idf.readChunk();
        byte[] data = chunk.getBytes();
        assertEquals("Chunk size preservation expected", idf.getMaxPayloadSize(), data.length);
        for (byte b : data) {
            assertEquals("Byte should be control 'new page'", ControlBytes.NEW_PAGE, b);
        }

        idf.toIndex(1);
        chunk = idf.readChunk();
        data = chunk.getBytes();
        assertEquals("Chunk size preservation expected", idf.getMaxPayloadSize(), data.length);
        for (byte b : data) {
            assertEquals("Byte should be control 'new page'", ControlBytes.END_OF_MEDIUM, b);
        }
    }

    @Test
    public void testOverwriteAChunk() {
        File tempFile = TestConstants.getTestFile(("test_1_" + System.currentTimeMillis() + ".dat"));


        IndexedFile idf = new IndexedFile(tempFile);
        idf.writeBytes("this is a test".getBytes(), true);

        idf.toIndex(1);
        idf.writeBytes("Second chunk".getBytes(), true);

        Chunk chunk = idf.readChunk();
        byte[] decoded = chunk.getBytes();
        byte[] expected = "Second chunk".getBytes();
        assertEquals("Resultant chunk size not same as encoded", expected.length, decoded.length);

        for (int i = 0; i < decoded.length; i++) {
            assertEquals("Byte at index " + i + " is incorrect", expected[i], decoded[i]);
        }

        //	Validate we can go back a chunk
        idf.toIndex(0);
        chunk = idf.readChunk();
        decoded = chunk.getBytes();
        expected = "this is a test".getBytes();
        assertEquals("Resultant chunk size not same as encoded", expected.length, decoded.length);

        for (int i = 0; i < decoded.length; i++) {
            assertEquals("Byte at index " + i + " is incorrect", expected[i], decoded[i]);
        }

        //	Now overwrite the first chunk!
        idf.toIndex(0);
        String newString = "Overwrite the first chunk with a totally new string";
        idf.writeBytes(newString.getBytes(), true);
        chunk = idf.readChunk();
        decoded = chunk.getBytes();
        expected = newString.getBytes();
        assertEquals("Resultant chunk size not same as encoded", expected.length, decoded.length);

        for (int i = 0; i < decoded.length; i++) {
            assertEquals("Byte at index " + i + " is incorrect", expected[i], decoded[i]);
        }
    }

    @Test
    public void testStoreSerializedObjectChunk() {
        File tempFile = TestConstants.getTestFile(("test_ser_" + System.currentTimeMillis() + ".dat"));


        ArrayList<String> secureList = new ArrayList<String>(Arrays.asList("Super", "Secret", "Stuff"));

        SecureDataUnit sda = new SecureDataUnit();
        sda.setData(Serialization.toBytes(secureList));

        IndexedFile idf = new IndexedFile(tempFile);
        idf.writeBytes(Serialization.toBytes(sda), true);
        IndexedFile.Chunk chunk = idf.readChunk();

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
    public void testEncryptedChunk() {

        File tempFile = TestConstants.getTestFile(("test_ser_enc" + System.currentTimeMillis() + ".dat"));


        ArrayList<String> strings = new ArrayList<String>(Arrays.asList("Larry", "Curly", "Moe"));
        SecureDataUnit sdu = new SecureDataUnit();
        sdu.setData(Serialization.toBytes(strings));

        byte[] encrypted = new DualLayerEncryptionService().encryptObject(password, sdu);


        IndexedFile idf = new IndexedFile(tempFile);
        idf.writeBytes(encrypted, true);
        IndexedFile.Chunk chunk = idf.readChunk();

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
    public void testSavingAFileOneUnit() {

        File tempFile = TestConstants.getTestFile(("test_single_unit" + System.currentTimeMillis() + ".dat"));


        try {
            IndexedFile idf = new IndexedFile(tempFile, false);
            idf.storeObject("testfile", new ArrayList<String>(Arrays.asList("LARRY", "CURLY", "MOE")));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error");
        }
    }

    @Test
    public void testSavingAFileOneUnitAndThenRecovering() {
        File tempFile = TestConstants.getTestFile(("test_rw_single_unit" + System.currentTimeMillis() + ".dat"));


        try {
            IndexedFile idf = new IndexedFile(tempFile, false);
            idf.storeObject("testfile", new ArrayList<String>(Arrays.asList("LARRY", "CURLY", "MOE")));

            idf = new IndexedFile(tempFile, false);
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
            IndexedFile idf = new IndexedFile(tempFile, true);
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
            idf = new IndexedFile(tempFile, true);
            view = idf.getFileView("test");

            view.addUnit(newUnit);

            idf = new IndexedFile(tempFile, true);
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
    public void testAllocateMultipleChunksBecauseObjectTooLarge() {

        int maxItem = 10000;    //	Make a huge object with list with this many items

        File tempFile = TestConstants.getTestFile(("test_rw_multi_unit" + System.currentTimeMillis() + ".dat"));

        try {
            IndexedFile idf = new IndexedFile(tempFile, false);

            ArrayList<String> reallyLongList = new ArrayList<String>();

            List<String> expectedItems = new ArrayList<String>();

            for (int i = 0; i < maxItem; i++) {
                String str = "TEST_" + System.nanoTime();
                reallyLongList.add(str);
                expectedItems.add(str);
            }

            idf.storeObject("testfile", reallyLongList);

            idf = new IndexedFile(tempFile, false);
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
            IndexedFile idf = new IndexedFile(tempFile, false);

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

            idf = new IndexedFile(tempFile, false);
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

    @Test
    public void testAllocateMultipleChunksCustomFileSizeBecauseObjectTooLarge() {

        int maxItem = 10000;    //	Make a huge object with list with this many items

        File tempFile = TestConstants.getTestFile(("test_rw_multi_unit" + System.currentTimeMillis() + ".dat"));

        try {
            IndexedFile idf = new IndexedFile(tempFile, 4096);

            ArrayList<String> reallyLongList = new ArrayList<String>();

            List<String> expectedItems = new ArrayList<String>();

            for (int i = 0; i < maxItem; i++) {
                String str = "TEST_" + System.nanoTime();
                reallyLongList.add(str);
                expectedItems.add(str);
            }

            idf.storeObject("testfile", reallyLongList);

            idf = new IndexedFile(tempFile, 4096);
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
    public void testAllocateAccrossMultipleFiles() {


        int maxItem = 10000;    //	Make a huge object with list with this many items

        File tempFile = TestConstants.getTestFile(("test_rw_multi_crisscross_unit" + System.currentTimeMillis() + ".dat"));


        String file1 = "file1";
        String file2 = "file2";

        try {
            IndexedFile idf = new IndexedFile(tempFile, false);

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
            idf = new IndexedFile(tempFile, false);
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
    public void testAllocateAccrossMultipleFilesCustomFileSize() {


        int maxItem = 10000;    //	Make a huge object with list with this many items

        File tempFile = TestConstants.getTestFile(("test_rw_multi_crisscross_unit" + System.currentTimeMillis() + ".dat"));


        String file1 = "file1";
        String file2 = "file2";

        try {
            IndexedFile idf = new IndexedFile(tempFile, 4096);

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
            idf = new IndexedFile(tempFile, 4096);
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
    public void testAllocateAccrossMultipleFilesAndThenDeleteOne() {


        int maxItem = 10000;    //	Make a huge object with list with this many items

        File tempFile = TestConstants.getTestFile(("test_rw_multi_crisscross_delete_unit" + System.currentTimeMillis() + ".dat"));


        String file1 = "file1";
        String file2 = "file2";

        AtomicInteger unitCounter = new AtomicInteger(1);

        try {
            IndexedFile idf = new IndexedFile(tempFile, false);

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
            idf = new IndexedFile(tempFile, false);
            idf.testMode = true;
            assertEquals("There should be 2 files in the system", 2, idf.listFiles().size());

            assertEquals("No units should be available on the file (system should have to create new ones)", 0, idf.fat.numFreeAllocations());

            //	Delete the first file
            idf.deleteFile(file1);

            assertTrue("Should be units available on the file system after deletion!", idf.fat.numFreeAllocations() > 0);

            assertEquals("Only 1 file expected now", 1, idf.listFiles().size());

            //	
            idf = new IndexedFile(tempFile, false);
            idf.testMode = true;
            assertEquals("Only 1 file expected now", 1, idf.listFiles().size());

            assertTrue("Should be units available on the file system after deletion!", idf.fat.numFreeAllocations() > 0);

            //	Try and overwrite existing blocks with another full 20k items
            ArrayList<String> toUpdate = (ArrayList<String>) idf.loadFile(file2);
            for (int i = 0; i < maxItem * 2; i++) {
                toUpdate.add("LST2_" + System.nanoTime() + "_UNIT_" + unitCounter.getAndIncrement());
            }

            idf.storeObject(file2, toUpdate);
            idf = new IndexedFile(tempFile, false);
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

    @Test
    public void testMiniOverrunFirstFATBlock() {

        String jibberish;
        byte[] jibberishBytes = new byte[10240];
        Entropy.get().fillBytes(jibberishBytes);
        jibberish = new String(jibberishBytes);


        int maxItem = 100;    //	Make a huge object with list with this many items

        File tempFile = TestConstants.getTestFile(("test_rw_force_fat_expansion" + System.currentTimeMillis() + ".dat"));


        try {
            IndexedFile idf = new IndexedFile(tempFile, 4096);

            int numJunkDataItems = 2;
            for (int k = 0; k < maxItem; k++) {    //	Store junk data 1000 times
                ArrayList<String> reallyLongList = new ArrayList<String>();
                for (int i = 0; i < numJunkDataItems; i++) {
                    String str = "TEST_" + System.nanoTime() + "_______________" + System.nanoTime() + jibberish;
                    reallyLongList.add(str);

                }

                idf.storeObject("testfile_____________" + k, reallyLongList);
            }

            idf = new IndexedFile(tempFile, 4096);
            List<String> files = idf.listFiles();

            assertEquals(maxItem + " files expected", maxItem, files.size());

            for (int i = 0; i < maxItem; i++) {
                List<String> recovered = (List<String>)
                        idf.loadFile(files.get(i));

                assertEquals("Size of items not same", numJunkDataItems, recovered.size());
            }

            FAT fat = (FAT) idf.loadFile(FAT.FILENAME);
            fat.initialize();
            assertTrue("Illegitimate test.  FAT cannot be shown to span multiple units!", fat._unitsAllocated(FAT.FILENAME).size() > 1);

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error");
        }

    }


    @Test
    public void testMiniOverrunFirstFATBlockAndRenameFilesDuringThis() {

        String jibberish;
        byte[] jibberishBytes = new byte[10240];
        Entropy.get().fillBytes(jibberishBytes);
        jibberish = new String(jibberishBytes);


        int maxItem = 100;    //	Make a huge object with list with this many items

        File tempFile = TestConstants.getTestFile(("test_rw_force_fat_expansion" + System.currentTimeMillis() + ".dat"));


        try {
            IndexedFile idf = new IndexedFile(tempFile, 4096);

            int numJunkDataItems = 2;
            for (int k = 0; k < maxItem; k++) {    //	Store junk data 1000 times
                ArrayList<String> reallyLongList = new ArrayList<String>();
                for (int i = 0; i < numJunkDataItems; i++) {
                    String str = "TEST_" + System.nanoTime() + "_______________" + System.nanoTime() + jibberish;
                    reallyLongList.add(str);

                }

                idf.storeObject("testfile_____________" + k, reallyLongList);

                idf.rename("testfile_____________" + k, "renamed_" + k);
            }

            idf = new IndexedFile(tempFile, 4096);
            List<String> files = idf.listFiles();

            assertEquals(maxItem + " files expected", maxItem, files.size());

            for (int i = 0; i < maxItem; i++) {
                assertFalse("One or more files was not properly renamed!", files.get(i).startsWith("testfile"));
                List<String> recovered = (List<String>)
                        idf.loadFile(files.get(i));

                assertEquals("Size of items not same", numJunkDataItems, recovered.size());
            }

            FAT fat = (FAT) idf.loadFile(FAT.FILENAME);
            fat.initialize();
            assertTrue("Illegitimate test.  FAT cannot be shown to span multiple units!", fat._unitsAllocated(FAT.FILENAME).size() > 1);

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error");
        }

    }

    //	Simulate reducing a file in size.  The medium should in turn reclaim previous units for storage
    @Test
    public void testAllocateAccrossMultipleFilesAndThenTruncateOne() {

        FileSystemTestListener listener = new DefaultFileSystemTestListener();


        int maxItem = 10000;    //	Make a huge object with list with this many items

        File tempFile = TestConstants.getTestFile(("test_rw_multi_crisscross_truncate_unit" + System.currentTimeMillis() + ".dat"));


        String file1 = "file1";
        String file2 = "file2";

        AtomicInteger unitCounter = new AtomicInteger(1);

        try {
            IndexedFile idf = new IndexedFile(tempFile, false);
            idf.setFileSystemTestListener(listener);

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
            idf = new IndexedFile(tempFile, false);
            idf.setFileSystemTestListener(listener);
            idf.testMode = true;
            assertEquals("There should be 2 files in the system", 2, idf.listFiles().size());

            assertEquals("No units should be available on the file (system should have to create new ones)", 0, idf.fat.numFreeAllocations());

            //	Shorten/truncate the first file
            idf.storeObject(file1, new ArrayList<String>());    //	Totally empty list this time

            assertTrue("Should be units available on the file system after truncation!", idf.fat.numFreeAllocations() > 0);

            assertEquals(" 2 files still expected ", 2, idf.listFiles().size());

            //	
            idf = new IndexedFile(tempFile, false);
            idf.setFileSystemTestListener(listener);
            idf.testMode = true;
            assertEquals(" 2 files still expected ", 2, idf.listFiles().size());

            assertTrue("Should be units available on the file system after deletion!", idf.fat.numFreeAllocations() > 0);

            //	Try and overwrite existing blocks with another full 20k items
            ArrayList<String> toUpdate = (ArrayList<String>) idf.loadFile(file2);
            for (int i = 0; i < maxItem * 2; i++) {
                toUpdate.add("LST2_" + System.nanoTime() + "_UNIT_" + unitCounter.getAndIncrement());
            }

            idf.storeObject(file2, toUpdate);
            idf = new IndexedFile(tempFile, false);
            idf.setFileSystemTestListener(listener);
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

            System.out.println(listener.getAverageTimesReport());

            assertTrue("Performance:  Avg write time > 50 ms", listener.getAverageWriteTime() < 50);
            assertTrue("Performance:  Avg read time > 50 ms", listener.getAverageReadTime() < 50);

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

        IndexedFile idf = new IndexedFile(tempFile, false);
        idf.importFile(TestConstants.TEST_RES_IMG_1, "1.jpg");
        idf.importFile(TestConstants.TEST_RES_IMG_2, "2.jpg");
        idf.importFile(TestConstants.TEST_RES_IMG_3, "3.jpg");

        idf.deleteFiles("1.jpg", "2.jpg");

        assertEquals("Single file remaining expected", 1, idf.listFiles().size());

    }

    @Test
    public void testDeleteMultipleFilesLoadOne() throws Exception {

        File tempFile = TestConstants.getTestFile("test_jpgimport" + System.currentTimeMillis() + ".dat");
        tempFile.deleteOnExit();
        tempFile.deleteOnExit();

        byte[] expectedBytes =
                Bytes.loadBytesFromFile(TestConstants.TEST_RES_IMG_3);

        IndexedFile idf = new IndexedFile(tempFile, false);
        idf.importFile(TestConstants.TEST_RES_IMG_1, "1.jpg");
        idf.importFile(TestConstants.TEST_RES_IMG_2, "2.jpg");
        idf.importFile(TestConstants.TEST_RES_IMG_3, "3.jpg");

        idf.deleteFiles("1.jpg", "2.jpg");

        byte[] loaded = idf.loadBytesFromFile("3.jpg");
        for (int i = 0; i < expectedBytes.length; i++) {
            assertEquals("Data corruption at byte " + i, expectedBytes[i], loaded[i]);
        }

    }

    @Test
    public void testPreventDeleteSameFileTwice() throws Exception {
        File tempFile = TestConstants.getTestFile("test_jpgimport" + System.currentTimeMillis() + ".dat");
        tempFile.deleteOnExit();
        tempFile.deleteOnExit();

        IndexedFile idf = new IndexedFile(tempFile, false);
        idf.importFile(TestConstants.TEST_RES_IMG_1, "1.jpg");
        idf.importFile(TestConstants.TEST_RES_IMG_2, "2.jpg");
        idf.importFile(TestConstants.TEST_RES_IMG_3, "3.jpg");

        try {
            idf.deleteFiles("1.jpg", "1.jpg");
            fail("System should not have allowed deleting same file twice");
        } catch (ChunkedMediumException cmx) {
            System.out.println(cmx.getMessage());
        }
    }

    //	Validate importing files from disk into an indexed file
    @Test
    public void testImportFile() {

        FileSystemTestListener listener = new DefaultFileSystemTestListener();

        File tempFile = TestConstants.getTestFile("test_jpgimport" + System.currentTimeMillis() + ".dat");
        tempFile.deleteOnExit();

        try {

            byte[] expectedBytes =
                    Bytes.loadBytesFromFile(TestConstants.TEST_RES_IMG_1);

            IndexedFile idf = new IndexedFile(tempFile, false);
            idf.setFileSystemTestListener(listener);
            assertEquals(
                    "System should return filename generated during import",
                    TestConstants.TEST_RES_IMG_1.getName(), idf.importFile(TestConstants.TEST_RES_IMG_1, null));

            idf = new IndexedFile(tempFile, false);
            idf.setFileSystemTestListener(listener);
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

            System.out.println(listener.getAverageTimesReport());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected error importing");
        }

    }

}
