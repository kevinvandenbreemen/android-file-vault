package com.vandenbreemen.secretcamera.di.mvp

import com.vandenbreemen.secretcamera.NoteDetailsActivity
import com.vandenbreemen.secretcamera.SELECTED_STRING
import com.vandenbreemen.secretcamera.StringSelection
import com.vandenbreemen.secretcamera.mvp.impl.notes.NoteDetailsModel
import com.vandenbreemen.secretcamera.mvp.impl.notes.NoteDetailsPresenterImpl
import com.vandenbreemen.secretcamera.mvp.notes.NoteDetailsPresenter
import dagger.Module
import dagger.Provides

/**
 * Created by kevin on 24/03/18.
 */
@Module
class NoteDetailsPresenterModule {

    @Provides
    fun providePresenter(activity:NoteDetailsActivity):NoteDetailsPresenter{
        val stringSelection = activity.intent.getParcelableExtra<StringSelection>(SELECTED_STRING)!!
        return NoteDetailsPresenterImpl(
                NoteDetailsModel(stringSelection.credentials!!, stringSelection.selectedString),
                activity
        )
    }

}