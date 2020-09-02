package com.vandenbreemen.secretcamera.di.mvp

import com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions.SFSActionsModel
import com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions.SFSActionsPresenter
import com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions.SFSActionsPresenterImpl
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.secretcamera.SFSActionsActivity
import dagger.Module
import dagger.Provides

/**
 *
 * @author kevin
 */
@Module
class SFSActionsModule {

    @Provides
    fun providesSFSActionsPresenter(activity: SFSActionsActivity): SFSActionsPresenter {
        return SFSActionsPresenterImpl(activity, activity,
                SFSActionsModel( activity.intent.getParcelableExtra(SFSCredentials.PARM_CREDENTIALS)!!))
    }

}