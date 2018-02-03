package com.vandenbreemen.mobilesecurestorage.file;

/**
 * <h2>Intro</h2>
 * <p>Error arising from logic using {@link IndexedFile}s
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class ChunkedMediumException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = -4252769573744072208L;

    public ChunkedMediumException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ChunkedMediumException(String detailMessage) {
        super(detailMessage);
    }
}
