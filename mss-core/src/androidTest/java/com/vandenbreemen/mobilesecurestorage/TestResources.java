package com.vandenbreemen.mobilesecurestorage;

import java.io.File;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class TestResources {


    /**
     * Location at which to store files during tests
     */
    public static final String TEST_DIR = "testOutput";
    public static final String FILENAME_RES_IMG_1 = "bright-red-sunset.jpg";
    public static final String FILENAME_RES_IMG_2 = "night-fog.jpg";
    public static final String FILENAME_RES_IMG_3 = "tractor.jpg";
    private static String testResourcePath = "./testResources";
    //  Images for testing file imports
    public static final String TEST_RES_IMG_1 = (testResourcePath + File.separator + FILENAME_RES_IMG_1);
    public static final String TEST_RES_IMG_2 = (testResourcePath + File.separator + FILENAME_RES_IMG_2);
    public static final String TEST_RES_IMG_3 = (testResourcePath + File.separator + FILENAME_RES_IMG_3);

}
