package com.vandenbreemen.secretcamera.di.activity

import com.vandenbreemen.secretcamera.ProjectDetailsActivity
import dagger.Module
import dagger.Provides

@Module
class ProjectDetailsActivityModule(private val activity: ProjectDetailsActivity) {

    @Provides
    fun provide(): ProjectDetailsActivity{
        return activity
    }

}