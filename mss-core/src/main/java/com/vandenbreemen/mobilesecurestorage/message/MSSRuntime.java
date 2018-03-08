package com.vandenbreemen.mobilesecurestorage.message;

import java.util.HashMap;
import java.util.Map;

/**
 * <h2>Intro</h2>
 * <p>Standard runtime exception for use in this application
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class MSSRuntime extends RuntimeException {

    /**
     * Attributes this exception can carry with it
     */
    private Map<String, Object> attributes;

    public MSSRuntime(String message) {
        super(message);
        this.attributes = new HashMap<>();
    }

    public MSSRuntime(String message, Throwable cause) {
        super(message, cause);
        this.attributes = new HashMap<>();
    }

    /**
     * Set attribute on this exception
     *
     * @param attr
     * @param value
     * @return
     */
    public MSSRuntime setAttribute(String attr, Object value) {
        this.attributes.put(attr, value);
        return this;
    }

    public Object getAttribute(String attr) {
        return this.attributes.get(attr);
    }
}
