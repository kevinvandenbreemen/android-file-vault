package com.vandenbreemen.secretcamera.di.activity

import com.vandenbreemen.secretcamera.TakeNoteActivity
import dagger.Module
import dagger.Provides

@Module
class TakeNoteActivityModule(private val takeNoteActivity: TakeNoteActivity) {

    @Provides
    fun getActivity(): TakeNoteActivity {
        return takeNoteActivity
    }

}