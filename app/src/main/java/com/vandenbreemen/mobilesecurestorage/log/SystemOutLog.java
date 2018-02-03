package com.vandenbreemen.mobilesecurestorage.log;

import java.util.Date;

/**
 * <h2>Intro</h2>
 * <p>Default logging.  Uses the system out to add logging statements
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class SystemOutLog implements CurrentSystemLog {

    private void printMsg(String message) {
        System.out.println(new Date().toString() + " - " + message);    //  NOSONAR Uses system out AS the logger
    }

    @Override
    public void info(String message) {
        printMsg("INFO - " + message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        printMsg("ERROR - " + message);
        throwable.printStackTrace();    //  NOSONAR Basic logging using system.err
    }

    @Override
    public void error(String message) {
        printMsg("ERROR - " + message);
    }

    @Override
    public void debug(String message) {
        printMsg("DEBUG - " + message);
    }

    @Override
    public void warn(String message) {
        printMsg("WARN - " + message);
    }
}
