package com.vandenbreemen.secretcamera.di.mvp

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.secretcamera.PictureViewerActivity
import com.vandenbreemen.secretcamera.mvp.gallery.PictureViewerModel
import com.vandenbreemen.secretcamera.mvp.gallery.PictureViewerPresenter
import com.vandenbreemen.secretcamera.mvp.gallery.PictureViewerPresenterImpl
import dagger.Module
import dagger.Provides

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
@Module
class PictureViewerPresenterModule {

    @Provides
    fun providesPictureViewerPresenter(activity: PictureViewerActivity): PictureViewerPresenter {
        return PictureViewerPresenterImpl(
                PictureViewerModel(activity.intent.getParcelableExtra<SFSCredentials>(SFSCredentials.PARM_CREDENTIALS) as SFSCredentials),
                activity,
                activity
        )
    }

}