package com.vandenbreemen.secretcamera.di

import android.app.Activity
import android.content.Context
import com.vandenbreemen.secretcamera.app.SecureCameraApp
import com.vandenbreemen.secretcamera.di.mvp.TakeNotePresenterModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 * Created by kevin on 24/03/18.
 */
@Singleton
@Component(modules = [
    AppModule::class,
    BuildersModule::class,
    TakeNotePresenterModule::class
])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance takeNotePresenterModule: TakeNotePresenterModule, @BindsInstance applicationContext: Context): AppComponent
    }

    //@Component.Builder
    interface Builder{
        @BindsInstance
        fun application(app:SecureCameraApp):Builder
        fun build():AppComponent
    }

    fun inject(app:SecureCameraApp)

    fun inject(activity: Activity)
}