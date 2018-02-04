package com.vandenbreemen.mobilesecurestorage.log;

import org.junit.Test;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class SystemLogTest {

    //  Validate the system can log using system out by default (to make it easier to debug our tests)
    @Test
    public void sanityTestUsesSystemOutByDefault() {
        SystemLog.get().info("This is a test"); //  This will crash the test if it defers to Android/is not set
    }

    @Test
    public void testInfoLogFormattedString() {
        SystemLog.get().info("This is a {}", "test");
    }

    @Test
    public void testDebugLogFormattedString() {
        SystemLog.get().debug("This is a {}", "test");
    }

    @Test
    public void testWarnLogFormattedString() {
        SystemLog.get().warn("This is a {}", "test");
    }

    @Test
    public void testErrorLogFormattedString() {
        SystemLog.get().error("This is a {}", "test");
    }

    @Test
    public void testErrorLogFormattedStringWithThrowable() {
        SystemLog.get().error("This is a {}", new Throwable(), "test");
    }

}
