package com.vandenbreemen.secretcamera.di.activity

import com.vandenbreemen.secretcamera.SFSActionsActivity
import dagger.Module
import dagger.Provides

@Module
class SFSActionsActivityModule(private val activity: SFSActionsActivity) {

    @Provides
    fun provide(): SFSActionsActivity {
        return activity
    }

}