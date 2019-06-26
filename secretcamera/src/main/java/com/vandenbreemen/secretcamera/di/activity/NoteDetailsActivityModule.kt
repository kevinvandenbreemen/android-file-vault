package com.vandenbreemen.secretcamera.di.activity

import com.vandenbreemen.secretcamera.NoteDetailsActivity
import dagger.Module
import dagger.Provides

@Module
class NoteDetailsActivityModule(private val activity: NoteDetailsActivity) {

    @Provides
    fun provideActivity(): NoteDetailsActivity {
        return activity
    }

}