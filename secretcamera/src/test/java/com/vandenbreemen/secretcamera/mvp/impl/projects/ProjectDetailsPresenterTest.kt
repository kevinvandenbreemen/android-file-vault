package com.vandenbreemen.secretcamera.mvp.impl.projects

import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.api.Project
import com.vandenbreemen.secretcamera.api.Task
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
        `when`(projectDetailsModel.getProjectTitle()).thenReturn(project.title)

    }

    @Test
    fun shouldDisplayProjectDescriptionOnStart() {

        //  Arrange
        projectDetailsPresenter.start()

        //  Act
        verify(projectDetailsView).showDescription(project.details)

    }

    @Test
    fun shouldDisplayProjectNameOnStart() {
        //  Arrange
        projectDetailsPresenter.start()

        //  Act
        verify(projectDetailsView).showName(project.title)
    }

    @Test
    fun shouldShowTaskDetailsWhenSelectingToAddTask() {
        //  arrange
        projectDetailsPresenter.start()

        //  Act
        projectDetailsPresenter.selectAddTask()

        //  Assert
        verify(projectDetailsRouter).showTaskDetails(null)
    }

    @Test
    fun shouldAddTaskToProject() {
        //  Arrange
        projectDetailsPresenter.start()
        projectDetailsPresenter.selectAddTask()
        val task = Task("Test Task")
        `when`(projectDetailsModel.addTask(task)).thenReturn(Single.just(listOf(task)))

        //  Act
        projectDetailsPresenter.submitTaskDetails(task)

        //  Assert
        verify(projectDetailsModel).addTask(task)
        verify(projectDetailsView).displayTasks(listOf(task))
    }

    @Test
    fun shouldShowErrorIfOccursDuringAddTask() {
        //  Arrange
        projectDetailsPresenter.start()
        projectDetailsPresenter.selectAddTask()
        val task = Task("")
        `when`(projectDetailsModel.addTask(task)).thenReturn(Single.error(ApplicationError("Missing task text")))

        //  Act
        projectDetailsPresenter.submitTaskDetails(task)

        //  Assert
        verify(projectDetailsView).showError(ApplicationError("Missing task text"))
    }

}