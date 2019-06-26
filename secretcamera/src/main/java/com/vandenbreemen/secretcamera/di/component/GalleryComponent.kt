package com.vandenbreemen.secretcamera.di.component

import com.vandenbreemen.secretcamera.Gallery
import com.vandenbreemen.secretcamera.di.activity.GalleryActivityModule
import com.vandenbreemen.secretcamera.di.mvp.GalleryPresenterModule
import dagger.Component

@Component(modules = [GalleryActivityModule::class, GalleryPresenterModule::class])
interface GalleryComponent {

    @Component.Factory
    interface Factory {
        fun create(activityModule: GalleryActivityModule, presenterModule: GalleryPresenterModule): GalleryComponent
    }

    fun inject(gallery: Gallery)

}