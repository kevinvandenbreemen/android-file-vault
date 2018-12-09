package com.vandenbreemen.secretcamera.mvp.impl.projects

import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Presenter
import com.vandenbreemen.secretcamera.api.Project
import com.vandenbreemen.secretcamera.mvp.projects.ProjectListPresenter
import com.vandenbreemen.secretcamera.mvp.projects.ProjectListRouter
import com.vandenbreemen.secretcamera.mvp.projects.ProjectListView

class ProjectListPresenterImpl(val model: ProjectListModel, val view: ProjectListView, val router: ProjectListRouter) : Presenter<ProjectListModel, ProjectListView>(model, view), ProjectListPresenter {
    override fun setupView() {
        model.getProjects().subscribe { projects ->
            view.showProjects(projects)
        }
    }

    override fun close() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isClosed(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addProject(project: Project) {
        model.addNewProject(project).subscribe({
            model.getProjects().subscribe { projects ->
                view.showProjects(projects)
            }
        }, {error->})
    }

}
