package com.vandenbreemen.secretcamera.di

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.secretcamera.MainActivity
import com.vandenbreemen.secretcamera.TakeNoteActivity
import dagger.Module
import dagger.Provides

/**
 * Created by kevin on 24/03/18.
 */
@Module
class SFSCredentialsModule {

    @Provides
    fun provideCredentials(activity:TakeNoteActivity):SFSCredentials{
        return activity.intent.getParcelableExtra<SFSCredentials>(SFSCredentials.PARM_CREDENTIALS)
            as SFSCredentials
    }

}