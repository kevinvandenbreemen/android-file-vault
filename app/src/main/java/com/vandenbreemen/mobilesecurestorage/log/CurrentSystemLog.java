package com.vandenbreemen.mobilesecurestorage.log;

/**
 * API for logging into various subsystems
 * Created by kevin
 */
public interface CurrentSystemLog {
    void info(String message);

    void error(String message, Throwable throwable);

    void error(String message);

    void debug(String message);

    void warn(String message);
}
