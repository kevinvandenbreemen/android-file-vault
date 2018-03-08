package com.vandenbreemen.mobilesecurestorage.file;

import com.vandenbreemen.mobilesecurestorage.log.slf4j.MessageFormatter;

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
    private TYPE type;

    public ChunkedMediumException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ChunkedMediumException(String detailMessage, Object... args) {
        super(MessageFormatter.arrayFormat(detailMessage, args).getMessage());
    }

    public TYPE getType() {
        return type;
    }

    public ChunkedMediumException setType(TYPE type) {
        this.type = type;
        return this;
    }

    /**
     * Type of error
     */
    public enum TYPE {
        FILE_NOT_FOUND,
    }
}
