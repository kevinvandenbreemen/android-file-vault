package com.vandenbreemen.secretcamera.di

import com.vandenbreemen.secretcamera.TakeNoteActivity
import com.vandenbreemen.secretcamera.di.activity.TakeNoteActivityModule
import com.vandenbreemen.secretcamera.di.component.DaggerTakeNoteComponent
import com.vandenbreemen.secretcamera.di.mvp.TakeNotePresenterModule


fun injectTakeNote(activity: TakeNoteActivity) {
    return DaggerTakeNoteComponent.factory().create(
            TakeNoteActivityModule(activity),
            TakeNotePresenterModule()
    ).inject(activity)
}