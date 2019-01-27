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
        projectDetailsView.displayTasks(projectDetailsModel.getTasks())
    }

    override fun selectAddTask() {
        projectDetailsRouter.showTaskDetails(null)
    }

    override fun submitUpdateTaskDetails(existingTask: Task, updateTaskData: Task) {
        try {
            projectDetailsModel.submitUpdateTaskDetails(existingTask, updateTaskData).subscribe({ tasks ->
                projectDetailsView.displayTasks(tasks)
            }, { error ->
                if (error is ApplicationError) {
                    projectDetailsView.showError(error)
                } else {
                    projectDetailsView.showError(ApplicationError("Unknown error occurred"))
                }
            })
        } catch (err: ApplicationError) {
            projectDetailsView.showError(err)
        }
    }

    override fun setCompleted(task: Task, completed: Boolean) {
        projectDetailsModel.markTaskCompleted(task, completed).subscribe { tasks ->
            projectDetailsView.displayTasks(tasks)
        }
    }

    override fun viewTask(task: Task) {
        projectDetailsRouter.showTaskDetails(task)
    }

    override fun submitTaskDetails(task: Task) {
        try {
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
        } catch (err: ApplicationError) {
            projectDetailsView.showError(err)
        }
    }
}
