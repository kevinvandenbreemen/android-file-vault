package com.vandenbreemen.secretcamera.di.activity

import com.vandenbreemen.secretcamera.TakePictureActivity
import dagger.Module
import dagger.Provides

@Module
class TakePictureActivityModule(private val activity: TakePictureActivity) {

    @Provides
    fun getActivity(): TakePictureActivity {
        return activity
    }

}