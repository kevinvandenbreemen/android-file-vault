package com.vandenbreemen.secretcamera.mvp.impl.projects

import com.vandenbreemen.secretcamera.api.Project
import com.vandenbreemen.secretcamera.mvp.projects.ProjectDetailsPresenter
import com.vandenbreemen.secretcamera.mvp.projects.ProjectDetailsRouter
import com.vandenbreemen.secretcamera.mvp.projects.ProjectDetailsView
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ProjectDetailsPresenterTest {

    lateinit var projectDetailsPresenter: ProjectDetailsPresenter

    @Mock
    lateinit var projectDetailsView: ProjectDetailsView

    @Mock
    lateinit var projectDetailsRouter: ProjectDetailsRouter

    @Mock
    lateinit var projectDetailsModel: ProjectDetailsModel

    val project = Project("Unit Test Project", "Project for validating the project details presenter!")

    @Before
    fun setup() {

        `when`(projectDetailsModel.init()).thenReturn(Single.just(Unit))

        projectDetailsPresenter = ProjectDetailsPresenterImpl(projectDetailsModel, projectDetailsView, projectDetailsRouter)

        `when`(projectDetailsModel.getDescription()).thenReturn(project.details)

    }

    @Test
    fun shouldDisplayProjectNameOnStart() {

        //  Arrange
        projectDetailsPresenter.start()

        //  Act
        verify(projectDetailsView).showDescription(project.details)

    }

}