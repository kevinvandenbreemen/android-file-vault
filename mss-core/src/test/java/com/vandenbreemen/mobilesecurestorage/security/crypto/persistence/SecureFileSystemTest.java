package com.vandenbreemen.mobilesecurestorage.security.crypto.persistence;

import com.vandenbreemen.mobilesecurestorage.TestConstants;
import com.vandenbreemen.mobilesecurestorage.data.Pair;
import com.vandenbreemen.mobilesecurestorage.file.ChunkedMediumException;
import com.vandenbreemen.mobilesecurestorage.file.ImportedFileData;
import com.vandenbreemen.mobilesecurestorage.file.IndexedFile;
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError;
import com.vandenbreemen.mobilesecurestorage.patterns.ProgressListener;
import com.vandenbreemen.mobilesecurestorage.security.Bytes;
import com.vandenbreemen.mobilesecurestorage.security.SecureString;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
public class SecureFileSystemTest {

    private static final long MAX_WRITE_TIME = 50;

    private static final long MAX_TIME = 25;


    @Test
    public void testSavingAFileOneUnit() {


        File tempFile = TestConstants.getTestFile(("test_single_unit" + System.currentTimeMillis() + ".dat"));
        tempFile.deleteOnExit();
        try {
            IndexedFile idf = getNewSecureFileSystem(tempFile);

            idf.storeObject("testfile", new ArrayList<String>(Arrays.asList("LARRY", "CURLY", "MOE")));

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error");
        }
    }

    @Test
    public void sanityTestPasswordCompareSuccess() {
        try {
            SecureString testPassword = new SecureString("password123".getBytes());
            File tempFile = TestConstants.getTestFile(("test_single_unit_passwd" + System.currentTimeMillis() + ".dat"));
            tempFile.deleteOnExit();
            SecureFileSystem sfs = (SecureFileSystem) getNewSecureFileSystem(tempFile);
            assertTrue("Same password should match key for secure file system",
                    sfs.testPassword(testPassword));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected error");
        }
    }

    @Test
    public void sanityTestPasswordCompareFailure() {
        try {
            SecureString testPassword = new SecureString("wrongpassword".getBytes());
            File tempFile = TestConstants.getTestFile(("test_single_unit_passwd" + System.currentTimeMillis() + ".dat"));
            tempFile.deleteOnExit();
            SecureFileSystem sfs = (SecureFileSystem) getNewSecureFileSystem(tempFile);
            assertFalse("Password mismatch expected",
                    sfs.testPassword(testPassword));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected error");
        }
    }

    //	Make sure that in spite of whatever changes are made the system remains backward-compatible

    @Test
    public void testSavingAFileOneUnitAndThenRecovering() {


        File tempFile = TestConstants.getTestFile(("test_rw_single_unit" + System.currentTimeMillis() + ".dat"));
        tempFile.deleteOnExit();
        try {
            IndexedFile idf = getNewSecureFileSystem(tempFile);

            idf.storeObject("testfile", new ArrayList<String>(Arrays.asList("LARRY", "CURLY", "MOE")));

            idf = getNewSecureFileSystem(tempFile);

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
    public void testAllocateMultipleChunksBecauseObjectTooLarge() {

        int maxItem = 10000;    //	Make a huge object with list with this many items


        File tempFile = TestConstants.getTestFile(("test_rw_multi_unit" + System.currentTimeMillis() + ".dat"));
        tempFile.deleteOnExit();
        try {
            IndexedFile idf = getNewSecureFileSystem(tempFile);


            ArrayList<String> reallyLongList = new ArrayList<String>();

            List<String> expectedItems = new ArrayList<String>();

            for (int i = 0; i < maxItem; i++) {
                String str = "TEST_" + System.nanoTime();
                reallyLongList.add(str);
                expectedItems.add(str);
            }

            idf.storeObject("testfile", reallyLongList);

            idf = getNewSecureFileSystem(tempFile);

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


    //	Try not to keep this one one if u don't need to.
    //@Test
    public void findOptimalChunkSize() {
        int maxItem = 10000;    //	Make a huge object with list with this many items
        ArrayList<String> reallyLongList = new ArrayList<String>();

        List<String> expectedItems = new ArrayList<String>();

        Map<Integer, Pair<Long, Long>> bestPerformanceNumbers = new LinkedHashMap<>();

        for (int i = 0; i < maxItem; i++) {
            String str = "TEST_" + System.nanoTime();
            reallyLongList.add(str);
            expectedItems.add(str);
        }

        for (int i = 0; i < 1000; i++) {

            int chunkSize = 4096 + (1024 * i);

            try {
                final SecureString pass11 = SecureFileSystem.generatePassword(new SecureString("abc".getBytes()));
                File tempFile = TestConstants.getTestFile(("opti" + System.currentTimeMillis() + ".dat"));
                tempFile.deleteOnExit();

                SecureFileSystem idf = new SecureFileSystem(tempFile) {

                    @Override
                    protected SecureString getPassword() {
                        return pass11;
                    }

                };

                System.out.println("CHUNK SIZE:  " + chunkSize);
                System.out.println("==========================");

                long start = System.currentTimeMillis();
                idf.storeObject("testfile", reallyLongList);
                long storeTime = (System.currentTimeMillis() - start);
                System.out.println("Store (custom):  " + storeTime);

                start = System.currentTimeMillis();
                idf.loadFile("testfile");
                long loadTime = (System.currentTimeMillis() - start);
                System.out.println("Load (custom):  " + loadTime);

                System.out.println("==========================");

                bestPerformanceNumbers.put(chunkSize, new Pair<Long, Long>(storeTime, loadTime));

            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error");
                ;
            }


        }

        StringBuilder csvBld = new StringBuilder("Chunk Size,Store Time,Load Time,\n");

        for (Integer k : bestPerformanceNumbers.keySet()) {
            csvBld.append(k).append(",").append(bestPerformanceNumbers.get(k).first()).append(",").append(bestPerformanceNumbers.get(k).second()).append(",");
            csvBld.append("\n");
        }

        System.out.println(csvBld);

    }

    //	This test causes blocks from two files to criss-cross
    @Test
    public void testAllocateAccrossMultipleFiles() {


        int maxItem = 10000;    //	Make a huge object with list with this many items

        File tempFile = TestConstants.getTestFile(("test_rw_multi_crisscross_unit" + System.currentTimeMillis() + ".dat"));
        tempFile.deleteOnExit();

        String file1 = "file1";
        String file2 = "file2";

        try {
            IndexedFile idf = getNewSecureFileSystem(tempFile);

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
            idf = getNewSecureFileSystem(tempFile);
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

    //	System must support deleting more than one filre
    @Test
    public void testDeleteMultipleFiles() throws Exception {
        File tempFile = TestConstants.getTestFile(("test_jpgimport" + System.currentTimeMillis() + ".dat"));
        tempFile.deleteOnExit();
        tempFile.deleteOnExit();

        IndexedFile idf = getNewSecureFileSystem(tempFile);
        idf.importFile(TestConstants.TEST_RES_IMG_1);
        idf.importFile(TestConstants.TEST_RES_IMG_2);
        idf.importFile(TestConstants.TEST_RES_IMG_3);

        idf.deleteFiles(TestConstants.TEST_RES_IMG_1.getName(), TestConstants.TEST_RES_IMG_2.getName());

        assertEquals("Single file remaining expected", 1, idf.listFiles().size());

    }

    @Test
    public void testChangePassword() throws Exception {
        File tempFile = TestConstants.getTestFile(("test_jpgimport" + System.currentTimeMillis() + ".dat"));
        tempFile.deleteOnExit();
        tempFile.deleteOnExit();

        SecureFileSystem idf = getNewSecureFileSystem(tempFile);
        idf.importFile(TestConstants.TEST_RES_IMG_1);
        idf.importFile(TestConstants.TEST_RES_IMG_2);
        idf.importFile(TestConstants.TEST_RES_IMG_3);

        SecureString secureString = SecureFileSystem.generatePassword(new SecureString("password123".getBytes()));

        idf.changePassword(
                new ProgressListener<Long>() {
                    @Override
                    public void setMax(Long progressMax) {
                        System.out.println("max=" + progressMax);
                    }

                    @Override
                    public void update(Long aLong) {
                        System.out.println("Progress=" + aLong);

                    }
                }, secureString);

        SecureFileSystem newlyLoaded = new SecureFileSystem(tempFile) {

            @Override
            protected SecureString getPassword() {
                return secureString;
            }
        };
        assertEquals("Files expected", 3, newlyLoaded.listFiles().size());
    }

    @Test
    public void testDeleteMultipleFilesLoadOne() throws Exception {

        File tempFile = TestConstants.getTestFile(("test_jpgimport" + System.currentTimeMillis() + ".dat"));
        tempFile.deleteOnExit();
        tempFile.deleteOnExit();

        byte[] expectedBytes =
                Bytes.loadBytesFromFile(TestConstants.TEST_RES_IMG_3);

        IndexedFile idf = getNewSecureFileSystem(tempFile);
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
    public void testAllocateAccrossMultipleFilesAndThenDeleteOne() {


        int maxItem = 10000;    //	Make a huge object with list with this many items

        File tempFile = TestConstants.getTestFile(("test_rw_multi_crisscross_delete_unit" + System.currentTimeMillis() + ".dat"));
        tempFile.deleteOnExit();

        String file1 = "file1";
        String file2 = "file2";

        AtomicInteger unitCounter = new AtomicInteger(1);

        try {
            IndexedFile idf = getNewSecureFileSystem(tempFile);


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
            idf = getNewSecureFileSystem(tempFile);


            assertEquals("There should be 2 files in the system", 2, idf.listFiles().size());


            //	Delete the first file
            idf.deleteFile(file1);


            assertEquals("Only 1 file expected now", 1, idf.listFiles().size());

            //
            idf = getNewSecureFileSystem(tempFile);

            assertEquals("Only 1 file expected now", 1, idf.listFiles().size());


            //	Try and overwrite existing blocks with another full 20k items
            ArrayList<String> toUpdate = (ArrayList<String>) idf.loadFile(file2);
            for (int i = 0; i < maxItem * 2; i++) {
                toUpdate.add("LST2_" + System.nanoTime() + "_UNIT_" + unitCounter.getAndIncrement());
            }

            idf.storeObject(file2, toUpdate);
            idf = getNewSecureFileSystem(tempFile);

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

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error");
        }

    }

    //	Simulate reducing a file in size.  The medium should in turn reclaim previous units for storage
    @Test
    public void testAllocateAccrossMultipleFilesAndThenTruncateOne() {


        int maxItem = 10000;    //	Make a huge object with list with this many items

        File tempFile = TestConstants.getTestFile(("test_rw_multi_crisscross_truncate_unit" + System.currentTimeMillis() + ".dat"));
        tempFile.deleteOnExit();

        String file1 = "file1";
        String file2 = "file2";

        AtomicInteger unitCounter = new AtomicInteger(1);

        try {
            IndexedFile idf = getNewSecureFileSystem(tempFile);

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
            idf = getNewSecureFileSystem(tempFile);
            assertEquals("There should be 2 files in the system", 2, idf.listFiles().size());


            //	Shorten/truncate the first file
            idf.storeObject(file1, new ArrayList<String>());    //	Totally empty list this time


            assertEquals(" 2 files still expected ", 2, idf.listFiles().size());

            //
            idf = getNewSecureFileSystem(tempFile);
            assertEquals(" 2 files still expected ", 2, idf.listFiles().size());


            //	Try and overwrite existing blocks with another full 20k items
            ArrayList<String> toUpdate = (ArrayList<String>) idf.loadFile(file2);
            for (int i = 0; i < maxItem * 2; i++) {
                toUpdate.add("LST2_" + System.nanoTime() + "_UNIT_" + unitCounter.getAndIncrement());
            }

            idf.storeObject(file2, toUpdate);
            idf = getNewSecureFileSystem(tempFile);
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


        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error");
        }

    }


    //	Validate importing files from disk into an indexed file
    @Test
    public void testImportFile() {


        File tempFile = TestConstants.getTestFile(("test_jpgimport" + System.currentTimeMillis() + ".dat"));
        tempFile.deleteOnExit();

        try {

            byte[] expectedBytes =
                    Bytes.loadBytesFromFile(TestConstants.TEST_RES_IMG_1);

            IndexedFile idf = getNewSecureFileSystem(tempFile);

            idf.importFile(TestConstants.TEST_RES_IMG_1);

            idf = getNewSecureFileSystem(tempFile);

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

    /**
     * Generate a new secure file system in test mode
     *
     * @param tempFile
     * @return
     * @throws ChunkedMediumException
     */
    private SecureFileSystem getNewSecureFileSystem(File tempFile)
            throws ChunkedMediumException {

        SecureString password = SecureFileSystem.generatePassword(new SecureString("password123".getBytes()));

        SecureFileSystem fs = new SecureFileSystem(tempFile) {
            protected SecureString getPassword() {
                return password;
            }

        };
        fs.setTestMode(true);
        return fs;
    }

    @Test
    public void testCopyKey() {
        SecureString secString = SecureFileSystem.generatePassword(new SecureString("abba".getBytes()));
        SecureString copy = SecureFileSystem.copyPassword(secString);

        assertTrue("Copy should exactly match original", copy.equals(secString));
    }

    /**
     * Validate concurrent access to the SFS, loading and saving files
     */
    //  This test is disabled as it is used only during development/troubleshooting
    @Test
    public void testConcurrentSFSAccess() throws Exception {

        File img1 = TestConstants.TEST_RES_IMG_1;
        File img2 = TestConstants.TEST_RES_IMG_2;
        File img3 = TestConstants.TEST_RES_IMG_3;
        List<File> filesList = Arrays.asList(img1, img2, img3);

        Map<String, byte[]> expectedBytesByFileName = new HashMap<>();
        expectedBytesByFileName.put(img1.getName(), Bytes.loadBytesFromFile(img1));
        expectedBytesByFileName.put(img2.getName(), Bytes.loadBytesFromFile(img2));
        expectedBytesByFileName.put(img3.getName(), Bytes.loadBytesFromFile(img3));

        File tempFile = TestConstants.getTestFile(("test_concurrent_access" + System.currentTimeMillis() + ".dat"));
        tempFile.deleteOnExit();
        SecureFileSystem sfs = (SecureFileSystem) getNewSecureFileSystem(tempFile);


        AtomicReference<Throwable> error = new AtomicReference<>();

        Thread importThread = new Thread() {
            @Override
            public void run() {
                if (Thread.currentThread().equals(this)) {
                    try {
                        while (true) {
                            filesList.forEach(file -> {
                                try {
                                    sfs.importFile(file);
                                    System.out.println("Imported " + file.getName());
                                } catch (ApplicationError wontHappen) {
                                }
                            });
                            break;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        error.set(ex);
                        return;
                    }
                }
            }
        };

        Thread deleteThread = new Thread() {
            @Override
            public void run() {
                try {
                    if (Thread.currentThread().equals(this)) {
                        while (true) {
                            sfs.listFiles().forEach(file -> {
                                try {
                                    sfs.deleteFile(file);
                                    System.out.println("Deleted " + file);
                                } catch (Exception ex) {
                                    throw new RuntimeException("Failed to delete " + file, ex);
                                }
                            });
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    error.set(ex);
                    return;
                }
            }
        };

        Thread readThread = new Thread() {
            @Override
            public void run() {
                try {
                    if (Thread.currentThread().equals(this)) {
                        while (true) {
                            sfs.listFiles().forEach(file -> {
                                try {
                                    ImportedFileData ifd = (ImportedFileData) sfs.loadFile(file);
                                    assertTrue("Byte Match", ByteUtils.equals(ifd.getFileData(), expectedBytesByFileName.get(file)));
                                    System.out.println("Read file " + file);
                                } catch (Exception ex) {
                                    if (ex instanceof ChunkedMediumException &&
                                            ChunkedMediumException.TYPE.FILE_NOT_FOUND.equals(((ChunkedMediumException) ex).getType())) {
                                        //  Can be safely ignored
                                        System.out.println("File " + file + " no longer exists");
                                    } else {
                                        throw new RuntimeException("Failed to load " + file, ex);
                                    }

                                }
                            });
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    error.set(ex);
                    return;
                }
            }
        };

        Thread readThread2 = new Thread() {
            @Override
            public void run() {
                try {
                    if (Thread.currentThread().equals(this)) {
                        while (true) {
                            List<String> fileNames = sfs.listFiles();
                            for (int i = fileNames.size() - 1; i >= 0; i--) {
                                String file = fileNames.get(i);
                                try {
                                    ImportedFileData ifd = (ImportedFileData) sfs.loadFile(file);
                                    assertTrue("Byte Match", ByteUtils.equals(ifd.getFileData(), expectedBytesByFileName.get(file)));
                                    System.out.println("Read file " + file);
                                } catch (Exception ex) {
                                    if (ex instanceof ChunkedMediumException &&
                                            ChunkedMediumException.TYPE.FILE_NOT_FOUND.equals(((ChunkedMediumException) ex).getType())) {
                                        //  Can be safely ignored
                                        System.out.println("File " + file + " no longer exists");
                                    } else {
                                        throw new RuntimeException("Failed to load " + file, ex);
                                    }

                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    error.set(ex);
                    return;
                }
            }
        };

        importThread.start();
        readThread.start();
        //deleteThread.start();
        readThread2.start();

        Thread.sleep(6000);

        importThread.interrupt();
        readThread.interrupt();
        readThread2.interrupt();
        deleteThread.interrupt();

        assertTrue("Error occurred", error.get() == null);
    }

    //  System needs to make itself unusable if a thread using it is unexpectedly interrupted
    //  This is to protect against loss of an indexed file that was created locally
    @Test
    public void testEmergencyCloseOnInterrupt() throws Exception {
        File tempFile = TestConstants.getTestFile(("test_jpgimport" + System.currentTimeMillis() + ".dat"));
        tempFile.deleteOnExit();

        SecureFileSystem idf = getNewSecureFileSystem(tempFile);

        idf.importFile(TestConstants.TEST_RES_IMG_1);

        try {
            idf.errorOutOnLockTimeout();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            idf.listFiles();
            fail("SFS operations should not be allowed anymore");
        } catch (Exception ex) {

        }

        try {
            idf.loadFile(TestConstants.TEST_RES_IMG_1.getName());
            fail("SFS operations should not be allowed anymore");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
