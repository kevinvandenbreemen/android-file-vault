package com.vandenbreemen.mobilesecurestorage.android.task;

import java.util.Optional;

/**
 * <h2>Intro
 * <p>Be sure to {@link #getError() check for error} before {@link #getResult() consuming the result}
 * <h2>Other Details
 *
 * @author kevin
 */
public class AsyncResult<T> {

    /**
     * Exception that might have occurred
     */
    private Exception error;

    /**
     * Actual computed result
     */
    private T result;

    /**
     * Result when an error occurred
     * @param error
     */
    public AsyncResult(Exception error) {
        this.error = error;
    }

    /**
     * Result when operation succeeded
     * @param result
     */
    public AsyncResult(T result) {
        this.result = result;
    }

    /**
     * Get error that might have occurred
     * @return  Optional that may have error
     */
    public Optional<Exception> getError(){
        return Optional.ofNullable(this.error);
    }

    /**
     * Get computed result
     * @return
     */
    public T getResult(){
        return this.result;
    }
}
