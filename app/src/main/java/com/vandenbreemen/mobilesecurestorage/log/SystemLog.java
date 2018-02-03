package com.vandenbreemen.mobilesecurestorage.log;

/**
 * <h2>Intro</h2>
 * <p>Universally accessible system log for use by classes throughout the framework
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class SystemLog {

    /**
     * Current system log
     */
    private static CurrentSystemLog __current = new SystemOutLog();

    /**
     * Sets the global system log
     *
     * @param __current
     */
    public static void setGlobalSystemLog(CurrentSystemLog __current) {
        SystemLog.__current = __current;
    }

    private SystemLog() {

    }

    public static CurrentSystemLog get() {
        return __current;
    }

}
