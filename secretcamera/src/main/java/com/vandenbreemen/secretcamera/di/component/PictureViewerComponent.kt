package com.vandenbreemen.secretcamera.di.component

import com.vandenbreemen.secretcamera.PictureViewerActivity
import com.vandenbreemen.secretcamera.di.activity.PictureViewerAcvitityModule
import com.vandenbreemen.secretcamera.di.mvp.PictureViewerPresenterModule
import dagger.Component

@Component(modules = [PictureViewerAcvitityModule::class, PictureViewerPresenterModule::class])
interface PictureViewerComponent {

    @Component.Factory
    interface Factory {
        fun create(acvitityModule: PictureViewerAcvitityModule, presenterModule: PictureViewerPresenterModule): PictureViewerComponent
    }

    fun inject(activity: PictureViewerActivity)

}