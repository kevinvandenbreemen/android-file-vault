package com.vandenbreemen.mobilesecurestorage.log;

import android.util.Log;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class AndroidSystemLog implements CurrentSystemLog {

    private static final String TAG = SystemLog.class.getSimpleName();

    @Override
    public void info(String message) {
        Log.i(TAG, message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        Log.e(TAG, message, throwable);
    }

    @Override
    public void error(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void debug(String message) {
        Log.d(TAG, message);
    }

    @Override
    public void warn(String message) {
        Log.w(TAG, message);
    }
}
