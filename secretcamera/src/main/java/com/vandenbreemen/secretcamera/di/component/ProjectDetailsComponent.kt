package com.vandenbreemen.secretcamera.di.component

import com.vandenbreemen.secretcamera.ProjectDetailsActivity
import com.vandenbreemen.secretcamera.di.activity.ProjectDetailsActivityModule
import com.vandenbreemen.secretcamera.di.mvp.ProjectDetailsModule
import dagger.Component

@Component(modules = [ProjectDetailsActivityModule::class, ProjectDetailsModule::class])
interface ProjectDetailsComponent {

    @Component.Factory
    interface Factory {
        fun create(acvitityModule: ProjectDetailsActivityModule, projectDetails: ProjectDetailsModule): ProjectDetailsComponent
    }

    fun inject(activity: ProjectDetailsActivity)

}