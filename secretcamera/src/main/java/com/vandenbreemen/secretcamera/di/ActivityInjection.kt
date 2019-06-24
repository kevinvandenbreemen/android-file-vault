package com.vandenbreemen.secretcamera.di

import com.vandenbreemen.secretcamera.TakeNoteActivity
import com.vandenbreemen.secretcamera.TakePictureActivity
import com.vandenbreemen.secretcamera.di.activity.TakeNoteActivityModule
import com.vandenbreemen.secretcamera.di.activity.TakePictureActivityModule
import com.vandenbreemen.secretcamera.di.component.DaggerTakeNoteComponent
import com.vandenbreemen.secretcamera.di.component.DaggerTakePictureComponent
import com.vandenbreemen.secretcamera.di.mvp.TakeNotePresenterModule
import com.vandenbreemen.secretcamera.di.mvp.TakePicturePresenterModule


fun injectTakeNote(activity: TakeNoteActivity) {
    return DaggerTakeNoteComponent.factory().create(
            TakeNoteActivityModule(activity),
            TakeNotePresenterModule()
    ).inject(activity)
}

fun injectTakePicture(activity: TakePictureActivity) {
    DaggerTakePictureComponent.factory().create(
            TakePictureActivityModule(activity),
            TakePicturePresenterModule()
    ).inject(activity)
}