package com.vandenbreemen.secretcamera.di.component

import com.vandenbreemen.secretcamera.TakeNoteActivity
import com.vandenbreemen.secretcamera.di.activity.TakeNoteActivityModule
import com.vandenbreemen.secretcamera.di.mvp.TakeNotePresenterModule
import dagger.Component

@Component(modules = [TakeNoteActivityModule:: class, TakeNotePresenterModule::class])
interface TakeNoteComponent {

    @Component.Factory
    interface Factory {
        fun create(activityModule: TakeNoteActivityModule, presenterModule: TakeNotePresenterModule): TakeNoteComponent
    }

    fun inject(activity: TakeNoteActivity)

}