package com.vandenbreemen.secretcamera.di.mvp

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.getDateInteractor
import com.vandenbreemen.secretcamera.TakePictureActivity
import com.vandenbreemen.secretcamera.mvp.takepicture.TakePictureModel
import com.vandenbreemen.secretcamera.mvp.takepicture.TakePicturePresenter
import com.vandenbreemen.secretcamera.mvp.takepicture.TakePicturePresenterImpl
import dagger.Module
import dagger.Provides


@Module
class TakePicturePresenterModule {

    @Provides
    fun providesTakePicturePresenter(activity: TakePictureActivity): TakePicturePresenter {
        return TakePicturePresenterImpl(
                TakePictureModel(getDateInteractor(), activity.intent.getParcelableExtra(SFSCredentials.PARM_CREDENTIALS)!!),
                activity
        )
    }

}