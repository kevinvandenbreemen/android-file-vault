package com.vandenbreemen.secretcamera.di.activity

import com.vandenbreemen.secretcamera.ProjectsActivity
import dagger.Module
import dagger.Provides

@Module
class ProjectsActivityModule (private val activity: ProjectsActivity){

    @Provides
    fun getActivity(): ProjectsActivity {
        return activity
    }

}