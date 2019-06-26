package com.vandenbreemen.secretcamera.di.component

import com.vandenbreemen.secretcamera.ProjectsActivity
import com.vandenbreemen.secretcamera.di.activity.ProjectsActivityModule
import com.vandenbreemen.secretcamera.di.mvp.ProjectManagementModule
import dagger.Component

@Component(modules = [ProjectManagementModule::class, ProjectsActivityModule::class])
interface ProjectsComponent {

    @Component.Factory
    interface Factory {
        fun create(projectManagementModule: ProjectManagementModule, projectsActivityModule: ProjectsActivityModule): ProjectsComponent
    }

    fun inject(projectsActivity: ProjectsActivity)

}