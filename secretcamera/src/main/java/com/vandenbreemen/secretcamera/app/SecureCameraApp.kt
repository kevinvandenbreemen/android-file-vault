package com.vandenbreemen.secretcamera.app

import android.app.Activity
import android.app.Application
import com.vandenbreemen.secretcamera.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

/**
 * Created by kevin on 24/03/18.
 */
class SecureCameraApp:Application(), HasActivityInjector {

    @Inject
    lateinit var
            dispatchingAndroidInjector:
            DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent
                .builder()
                .application(this)
                .build()
                .inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingAndroidInjector
    }
}