package com.vandenbreemen.secretcamera.di.mvp

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.secretcamera.Gallery
import com.vandenbreemen.secretcamera.mvp.gallery.GalleryPresenter
import com.vandenbreemen.secretcamera.mvp.impl.gallery.GalleryModel
import com.vandenbreemen.secretcamera.mvp.impl.gallery.GalleryPresenterImpl
import dagger.Module
import dagger.Provides

@Module
class GalleryPresenterModule {

    @Provides
    fun providePresenter(activity: Gallery): GalleryPresenter {
        val model = GalleryModel(activity.intent.getParcelableExtra(SFSCredentials.PARM_CREDENTIALS))
        return GalleryPresenterImpl(model, activity)
    }

}