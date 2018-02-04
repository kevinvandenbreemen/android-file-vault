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

}
