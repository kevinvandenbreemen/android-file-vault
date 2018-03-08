package com.vandenbreemen.mobilesecurestorage;

import java.io.File;
import java.io.IOException;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class TestConstants {

    /**
     * Location at which to store files during tests
     */
    public static final String TEST_DIR = "testOutput";

    //  Images for testing file imports
    public static final File TEST_RES_IMG_1 = new File("testResources" + File.separator + "bright-red-sunset.jpg");
    public static final File TEST_RES_IMG_2 = new File("testResources" + File.separator + "night-fog.jpg");
    public static final File TEST_RES_IMG_3 = new File("testResources" + File.separator + "tractor.jpg");

    /**
     * Gets a new test file.  The file will be deleted on program completion.
     *
     * @param name
     * @return
     */
    public static File getTestFile(String name) {

        File dir = new File(TEST_DIR);
        dir.mkdir();

        File file = new File(TEST_DIR + File.separator + name);
        try {
            file.createNewFile();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new RuntimeException("Unexpected - Unable to create test file!", ioe);
        }
        file.deleteOnExit();
        return file;
    }

}
