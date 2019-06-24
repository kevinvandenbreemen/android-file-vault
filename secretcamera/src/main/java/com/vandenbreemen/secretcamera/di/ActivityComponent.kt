package com.vandenbreemen.secretcamera.di

import android.app.Activity
import com.vandenbreemen.secretcamera.TakeNoteActivity
import com.vandenbreemen.secretcamera.di.activity.TakeNoteActivityModule
import com.vandenbreemen.secretcamera.di.component.DaggerTakeNoteComponent
import com.vandenbreemen.secretcamera.di.component.TakeNoteComponent
import dagger.BindsInstance
import dagger.Component

@Component
interface ActivityComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance activity: Activity): ActivityComponent
    }

    fun currentActivity(): Activity

}

fun ActivityComponent.takeNote(): TakeNoteComponent {
    return DaggerTakeNoteComponent.builder()
            .activityComponent(this)
            .takeNoteActivityModule(TakeNoteActivityModule(currentActivity() as TakeNoteActivity))
            .build()
}