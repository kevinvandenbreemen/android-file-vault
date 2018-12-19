package com.vandenbreemen.test

/**
 * Callback that allows for registering idling resources etc. into production code to allow for more
 * accurately testing page / screen transitions
 */
interface BackgroundCompletionCallback {

    /**
     * Start the background operation
     */
    fun onStart()

    /**
     * Finish the background operation
     */
    fun onFinish()

}