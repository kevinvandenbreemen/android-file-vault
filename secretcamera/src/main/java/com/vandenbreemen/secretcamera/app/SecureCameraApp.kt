package com.vandenbreemen.secretcamera.app

import android.app.Application
import com.vandenbreemen.secretcamera.di.DaggerAppComponent

/**
 * Created by kevin on 24/03/18.
 */
class SecureCameraApp:Application() {


    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent
                .factory()
                .create(applicationContext)
    }
}