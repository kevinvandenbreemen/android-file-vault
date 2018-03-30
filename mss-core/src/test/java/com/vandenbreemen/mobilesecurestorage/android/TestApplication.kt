package com.vandenbreemen.mobilesecurestorage.android

import android.app.Application
import com.vandenbreemen.mobilesecurestorage.R

/**
 * <h2>Intro</h2>
 * See https://stackoverflow.com/questions/32346748/robolectric-illegalstateexception-you-need-to-use-a-theme-appcompat-theme-or
 * <h2>Other Details</h2>
 * @author kevin
 */
class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.AppTheme)
    }

}