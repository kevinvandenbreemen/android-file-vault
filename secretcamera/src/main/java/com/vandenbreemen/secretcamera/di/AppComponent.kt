package com.vandenbreemen.secretcamera.di

import com.vandenbreemen.secretcamera.app.SecureCameraApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Created by kevin on 24/03/18.
 */
@Singleton
@Component(modules = [
    AppModule::class,
    BuildersModule::class
])
interface AppComponent {

    @Component.Builder
    interface Builder{
        @BindsInstance
        fun application(app:SecureCameraApp):Builder
        fun build():AppComponent
    }

    fun inject(app:SecureCameraApp)

}