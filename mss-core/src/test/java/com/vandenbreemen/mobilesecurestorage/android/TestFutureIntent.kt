package com.vandenbreemen.mobilesecurestorage.android

import android.content.Intent
import com.vandenbreemen.mobilesecurestorage.android.api.FutureIntent

class FakeValueProvider {}


class TestFutureIntent : FutureIntent<FakeValueProvider> {

    companion object {
        const val KEY_NAME = "KEYNAME"
        const val KEY_VAL = "KEYVALUE"
    }

    override fun populateIntentWithDetailsAboutFutureActivity(intent: Intent, provider: FakeValueProvider) {

    }

    override fun populateIntentToStartFutureActivity(intentToStartFutureActivity: Intent, intentForCurrentActivity: Intent) {
        intentToStartFutureActivity.putExtra(KEY_NAME, KEY_VAL)
    }

}