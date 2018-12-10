package com.vandenbreemen.secretcamera.di.mvp

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.secretcamera.ProjectsActivity
import com.vandenbreemen.secretcamera.mvp.impl.projects.ProjectListModel
import com.vandenbreemen.secretcamera.mvp.impl.projects.ProjectListPresenterImpl
import com.vandenbreemen.secretcamera.mvp.projects.ProjectListPresenter
import dagger.Module
import dagger.Provides

@Module
class ProjectManagementModule {

    @Provides
    fun providesProjectListPresenter(activity: ProjectsActivity): ProjectListPresenter{
        return ProjectListPresenterImpl(
                ProjectListModel(activity.intent.getParcelableExtra(SFSCredentials.PARM_CREDENTIALS)),
                activity,
                activity
        )
    }

}