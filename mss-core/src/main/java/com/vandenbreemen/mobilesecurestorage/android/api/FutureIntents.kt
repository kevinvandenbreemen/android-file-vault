package com.vandenbreemen.mobilesecurestorage.android.api

import android.content.Intent

interface FutureIntent<ValueProvider> {

    /**
     * Fill in details about the intent.
     */
    fun populateIntentWithDetailsAboutFutureActivity(intent: Intent, provider: ValueProvider)

    /**
     * Populate the details regarding how to start the future activity
     */
    fun populateIntentToStartFutureActivity(intentToStartFutureActivity: Intent, intentForCurrentActivity: Intent)

}