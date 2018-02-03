package com.vandenbreemen.mobilesecurestorage.message;

/**
 * <h2>Intro</h2>
 * <p>Standard runtime exception for use in this application
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class MSSRuntime extends RuntimeException {

    public MSSRuntime(String message) {
        super(message);
    }

    public MSSRuntime(String message, Throwable cause) {
        super(message, cause);
    }
}
