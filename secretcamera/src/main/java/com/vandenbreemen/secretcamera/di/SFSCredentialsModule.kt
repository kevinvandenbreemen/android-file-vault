package com.vandenbreemen.secretcamera.di

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.secretcamera.MainActivity
import dagger.Module
import dagger.Provides

/**
 * Created by kevin on 24/03/18.
 */
@Module
class SFSCredentialsModule {

    @Provides
    fun provideCredentials(activity:MainActivity):SFSCredentials{
        return activity.intent.getParcelableExtra<SFSCredentials>(SFSCredentials.PARM_CREDENTIALS)
            as SFSCredentials
    }

}