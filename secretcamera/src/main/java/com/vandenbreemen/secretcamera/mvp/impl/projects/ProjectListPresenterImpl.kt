package com.vandenbreemen.secretcamera.mvp.impl.projects

import com.vandenbreemen.mobilesecurestorage.log.SystemLog
import com.vandenbreemen.mobilesecurestorage.log.e
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Presenter
import com.vandenbreemen.secretcamera.api.Project
import com.vandenbreemen.secretcamera.mvp.projects.ProjectListPresenter
import com.vandenbreemen.secretcamera.mvp.projects.ProjectListRouter
import com.vandenbreemen.secretcamera.mvp.projects.ProjectListView

class ProjectListPresenterImpl(val model: ProjectListModel, val view: ProjectListView, val router: ProjectListRouter) : Presenter<ProjectListModel, ProjectListView>(model, view), ProjectListPresenter {


    override fun setupView() {
        addForDisposal(model.getProjects().subscribe { projects ->
            view.showProjects(projects)
        })
    }

    override fun viewProjectDetails(project: Project) {
        router.gotoProjectDetails(project.title, model.copyCredentials())
    }

    override fun addProject(project: Project) {
        addForDisposal(model.addNewProject(project).subscribe({
            model.getProjects().subscribe { projects ->
                view.showProjects(projects)
            }
        }, {error->
            if (error is ApplicationError) {
                view.showError(error)
            } else {
                SystemLog.get().e("CreateProject", "Error adding project", error)
                view.showError(ApplicationError("Unknown error occurred"))
            }
        }))
    }

}
