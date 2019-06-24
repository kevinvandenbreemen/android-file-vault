package com.vandenbreemen.secretcamera.di

import com.vandenbreemen.secretcamera.app.SecureCameraApp
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 * Created by kevin on 24/03/18.
 */
@Singleton
@Component()
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: SecureCameraApp): AppComponent
    }


}