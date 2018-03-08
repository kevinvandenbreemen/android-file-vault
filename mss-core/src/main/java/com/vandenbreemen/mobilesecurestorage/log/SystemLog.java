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
    private static CurrentSystemLog currentSystemLog = new SystemOutLog();

    private SystemLog() {

    }

    /**
     * Sets the global system log
     *
     * @param current
     */
    public static void setGlobalSystemLog(CurrentSystemLog current) {
        SystemLog.currentSystemLog = current;
    }

    public static CurrentSystemLog get() {
        return currentSystemLog;
    }

}
