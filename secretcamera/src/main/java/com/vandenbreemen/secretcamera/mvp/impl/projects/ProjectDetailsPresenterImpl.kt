package com.vandenbreemen.secretcamera.mvp.impl.projects

import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Presenter
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
}
