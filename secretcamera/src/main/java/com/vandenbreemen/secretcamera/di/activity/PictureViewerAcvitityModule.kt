package com.vandenbreemen.secretcamera.di.activity

import com.vandenbreemen.secretcamera.PictureViewerActivity
import dagger.Module
import dagger.Provides

@Module
class PictureViewerAcvitityModule(private val activity: PictureViewerActivity) {

    @Provides
    fun getActivity(): PictureViewerActivity {
        return activity
    }

}