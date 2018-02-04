package com.vandenbreemen.mobilesecurestorage.log;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * <h2>Intro</h2>
 * <p>Validate behaviour of the system log with the {@link AndroidSystemLog}
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
@RunWith(RobolectricTestRunner.class)
public class AndroidSystemLogTest {

    @BeforeClass
    public static void setup() {
        SystemLog.setGlobalSystemLog(new AndroidSystemLog());
    }

    @AfterClass
    public static void tearDown() {
        SystemLog.setGlobalSystemLog(new SystemOutLog());
    }

    @Test
    public void testLog() {
        SystemLog.get().info("This is a test");
    }

}
