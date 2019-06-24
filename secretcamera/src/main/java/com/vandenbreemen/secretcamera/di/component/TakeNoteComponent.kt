package com.vandenbreemen.secretcamera.di.component

import com.vandenbreemen.secretcamera.TakeNoteActivity
import com.vandenbreemen.secretcamera.di.ActivityComponent
import com.vandenbreemen.secretcamera.di.activity.TakeNoteActivityModule
import com.vandenbreemen.secretcamera.di.mvp.TakeNotePresenterModule
import dagger.Component

@Component(dependencies = [ActivityComponent::class], modules = [TakeNoteActivityModule:: class, TakeNotePresenterModule::class])
interface TakeNoteComponent {

    fun inject(activity: TakeNoteActivity)
    //fun getTakeNotePresenter(): TakeNewNotePresenter

}