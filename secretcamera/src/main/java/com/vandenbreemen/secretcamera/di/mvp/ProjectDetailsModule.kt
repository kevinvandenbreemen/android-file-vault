package com.vandenbreemen.secretcamera.di.mvp

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.secretcamera.ProjectDetailsActivity
import com.vandenbreemen.secretcamera.mvp.impl.projects.ProjectDetailsModel
import com.vandenbreemen.secretcamera.mvp.impl.projects.ProjectDetailsPresenterImpl
import com.vandenbreemen.secretcamera.mvp.projects.ProjectDetailsPresenter
import dagger.Module
import dagger.Provides

@Module
class ProjectDetailsModule {

    @Provides
    fun providesProjectDetailsPresenter(activity: ProjectDetailsActivity) : ProjectDetailsPresenter{
        return ProjectDetailsPresenterImpl(
                ProjectDetailsModel(activity.intent.getStringExtra(ProjectDetailsActivity.PARM_PROJECT_NAME)!!, activity.intent.getParcelableExtra(SFSCredentials.PARM_CREDENTIALS)!!),
                activity, activity
        )
    }

}