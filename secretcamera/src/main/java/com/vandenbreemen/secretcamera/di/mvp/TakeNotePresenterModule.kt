package com.vandenbreemen.secretcamera.di.mvp

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.secretcamera.TakeNoteActivity
import com.vandenbreemen.secretcamera.mvp.impl.TakeNewNoteModel
import com.vandenbreemen.secretcamera.mvp.impl.TakeNewNotePresenterImpl
import com.vandenbreemen.secretcamera.mvp.notes.TakeNewNotePresenter
import dagger.Module
import dagger.Provides

/**
 * Created by kevin on 24/03/18.
 */
@Module
class TakeNotePresenterModule {

    @Provides
    fun provideTakeNotePresenter(activity:TakeNoteActivity):TakeNewNotePresenter{
        return TakeNewNotePresenterImpl(
                activity,
                TakeNewNoteModel(activity.intent.getParcelableExtra(SFSCredentials.PARM_CREDENTIALS)!!)
        )
    }

}