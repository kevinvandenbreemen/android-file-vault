package com.vandenbreemen.mobilesecurestorage.patterns;

/**
 * <h2>Intro</h2>
 * <p>Listener for use when you would like to acquire progress from an operation that isn't aware of
 * Android classes like AsyncTask etc.
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public interface ProgressListener<Progress> {

    /**
     * Set max value of progress
     *
     * @param progressMax
     */
    public void setMax(Progress progressMax);

    /**
     * Set current value of progress
     *
     * @param progress
     */
    public void update(Progress progress);

}
