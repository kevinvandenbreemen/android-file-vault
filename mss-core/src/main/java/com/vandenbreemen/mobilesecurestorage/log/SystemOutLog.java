package com.vandenbreemen.mobilesecurestorage.log;

import com.vandenbreemen.mobilesecurestorage.log.slf4j.MessageFormatter;

import java.util.Date;

/**
 * <h2>Intro</h2>
 * <p>Default logging.  Uses the system out to add logging statements
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class SystemOutLog extends CurrentSystemLog {

    private void printMsg(String message, Object... args) {
        String msg = MessageFormatter.arrayFormat(message, args).getMessage();
        System.out.println(new Date().toString() + " - " + msg);    //  NOSONAR Uses system out AS the logger
    }

    @Override
    public void info(String message, Object... args) {
        printMsg("INFO - " + message, args);
    }

    @Override
    public void error(String message, Throwable throwable, Object... args) {
        printMsg("ERROR - " + message, args);
        throwable.printStackTrace();    //  NOSONAR Basic logging using system.err
    }

    @Override
    public void error(String message, Object... args) {
        printMsg("ERROR - " + message, args);
    }

    @Override
    public void debug(String message, Object... args) {
        printMsg("DEBUG - " + message, args);
    }

    @Override
    public void warn(String message, Object... args) {
        printMsg("WARN - " + message, args);
    }
}
