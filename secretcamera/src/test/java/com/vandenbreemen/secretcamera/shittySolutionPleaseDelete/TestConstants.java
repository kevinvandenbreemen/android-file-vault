package com.vandenbreemen.secretcamera.shittySolutionPleaseDelete;

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

    private static File testResourcePath = new File("../mss-core/testResources");
    //  Images for testing file imports
    public static final File TEST_RES_IMG_1 = new File(testResourcePath.getAbsolutePath() + File.separator + "bright-red-sunset.jpg");
    public static final File TEST_RES_IMG_2 = new File(testResourcePath.getAbsolutePath() + File.separator + "night-fog.jpg");
    public static final File TEST_RES_IMG_3 = new File(testResourcePath.getAbsolutePath() + File.separator + "tractor.jpg");
    public static final File TEST_RES_IMG_4 = new File(testResourcePath.getAbsolutePath() + File.separator + "smileyface.png");

    public static final File NON_IMAGE = new File(testResourcePath.getAbsolutePath() + File.separator + "README.md");

    static {
        if (!testResourcePath.exists()) {
            testResourcePath = new File("../mss-core" + File.separator + "testResources");
            if (!testResourcePath.exists()) {
                throw new RuntimeException("Unable to find path to test resources!  Tried " + testResourcePath.getAbsolutePath());
            }
        }
    }

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

    /**
     * @param name   Name of file
     * @param create Whether to actually create the file
     * @return
     */
    public static File getTestFile(String name, boolean create) {
        File dir = new File(TEST_DIR);
        dir.mkdir();

        File file = new File(TEST_DIR + File.separator + name);
        if (create) {
            try {
                file.createNewFile();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                throw new RuntimeException("Unexpected - Unable to create test file!", ioe);
            }
        }
        file.deleteOnExit();
        return file;
    }

}

