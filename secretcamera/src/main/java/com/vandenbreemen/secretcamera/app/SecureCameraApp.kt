package com.vandenbreemen.secretcamera.app

import android.app.Application
import com.vandenbreemen.secretcamera.di.AppComponent
import com.vandenbreemen.secretcamera.di.DaggerAppComponent
import com.vandenbreemen.secretcamera.di.mvp.TakeNotePresenterModule

/**
 * Created by kevin on 24/03/18.
 */
class SecureCameraApp:Application() {

    companion object {
        lateinit var component: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        component = DaggerAppComponent
                .factory()
                .create(TakeNotePresenterModule(),  applicationContext)
    }
}