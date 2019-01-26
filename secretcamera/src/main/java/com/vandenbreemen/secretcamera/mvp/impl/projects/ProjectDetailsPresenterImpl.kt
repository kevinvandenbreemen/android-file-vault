package com.vandenbreemen.secretcamera.mvp.impl.projects

import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Presenter
import com.vandenbreemen.secretcamera.api.Task
import com.vandenbreemen.secretcamera.mvp.projects.ProjectDetailsPresenter
import com.vandenbreemen.secretcamera.mvp.projects.ProjectDetailsRouter
import com.vandenbreemen.secretcamera.mvp.projects.ProjectDetailsView

class ProjectDetailsPresenterImpl(val projectDetailsModel: ProjectDetailsModel, val projectDetailsView: ProjectDetailsView, val projectDetailsRouter: ProjectDetailsRouter) :
        Presenter<ProjectDetailsModel, ProjectDetailsView>(projectDetailsModel, projectDetailsView),
        ProjectDetailsPresenter {


    override fun setupView() {
        projectDetailsView.showDescription(projectDetailsModel.getDescription())
        projectDetailsView.showName(projectDetailsModel.getProjectTitle())
    }

    override fun selectAddTask() {
        projectDetailsRouter.showTaskDetails(null)
    }

    override fun submitTaskDetails(task: Task) {
        projectDetailsModel.addTask(task).subscribe({ taskList ->
            projectDetailsView.displayTasks(taskList)
        }, { error ->
            if (error is ApplicationError) {
                projectDetailsView.showError(error)
            } else {
                error.printStackTrace()
                projectDetailsView.showError(ApplicationError("Unknown error occurred"))
            }
        })
    }
}
