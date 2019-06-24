package com.vandenbreemen.secretcamera.di

import android.content.Context
import com.vandenbreemen.secretcamera.app.SecureCameraApp
import dagger.BindsInstance
import dagger.Component
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

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): AppComponent
    }

    //@Component.Builder
    interface Builder{
        @BindsInstance
        fun application(app:SecureCameraApp):Builder
        fun build():AppComponent
    }

    fun inject(app:SecureCameraApp)

}