package com.vandenbreemen.mobilesecurestorage.android.api

import com.vandenbreemen.mobilesecurestorage.message.ApplicationError

/**
 * <h2>Intro</h2>
 * Behaviour of an object that can display an error
 * <h2>Other Details</h2>
 * @author kevin
 */
public interface ErrorDisplay {

    fun display(error: ApplicationError)

}