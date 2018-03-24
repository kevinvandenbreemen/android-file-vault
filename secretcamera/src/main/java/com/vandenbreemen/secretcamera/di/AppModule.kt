package com.vandenbreemen.secretcamera.di

import android.app.Application
import android.content.Context
import com.vandenbreemen.secretcamera.app.SecureCameraApp
import dagger.Module
import dagger.Provides

/**
 * Created by kevin on 24/03/18.
 */
@Module
class AppModule {

    @Provides
    fun provideContext(app: SecureCameraApp):Context{
        return app.applicationContext
    }



}