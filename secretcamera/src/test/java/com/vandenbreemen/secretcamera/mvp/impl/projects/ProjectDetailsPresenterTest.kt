package com.vandenbreemen.secretcamera.mvp.impl.projects

import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.api.Project
import com.vandenbreemen.secretcamera.api.Task
import com.vandenbreemen.secretcamera.mvp.projects.ProjectDetailsPresenter
import com.vandenbreemen.secretcamera.mvp.projects.ProjectDetailsRouter
import com.vandenbreemen.secretcamera.mvp.projects.ProjectDetailsView
import io.reactivex.Completable
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
    fun shouldLoadAndDisplayTasksOnStart() {
        //  Arrange
        `when`(projectDetailsModel.getTasks()).thenReturn(listOf(Task("Test Task 2"), Task("Test Task 2")))

        //  Act
        projectDetailsPresenter.start()

        //  Assert
        verify(projectDetailsView).displayTasks(listOf(Task("Test Task 2"), Task("Test Task 2")))
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

    @Test
    fun shouldShowErrorIfOccursDuringValidationBeforeAddTask() {
        //  Arrange
        projectDetailsPresenter.start()
        projectDetailsPresenter.selectAddTask()
        val task = Task("")
        `when`(projectDetailsModel.addTask(task)).thenThrow(ApplicationError("Task description is required"))

        //  Act
        projectDetailsPresenter.submitTaskDetails(task)

        //  Assert
        verify(projectDetailsView).showError(ApplicationError("Task description is required"))
    }

    @Test
    fun shouldViewTaskDetails() {
        //  Arrange
        projectDetailsPresenter.start()

        //  Act
        projectDetailsPresenter.viewTask(Task("Test Task"))

        //  Assert
        verify(projectDetailsRouter).showTaskDetails(Task("Test Task"))
    }

    @Test
    fun shouldUpdateTask() {
        //  Arrange
        projectDetailsPresenter.start()
        `when`(projectDetailsModel.submitUpdateTaskDetails(Task("Existing"), Task("Updated"))).thenReturn(Single.just(listOf(Task("Updated"))))

        //  Act
        projectDetailsPresenter.submitUpdateTaskDetails(Task("Existing"), Task("Updated"))

        //  Assert
        verify(projectDetailsModel).submitUpdateTaskDetails(Task("Existing"), Task("Updated"))
    }

    @Test
    fun shouldDisplayUpdatedTasksOnUpdateTasks() {
        //  Arrange
        projectDetailsPresenter.start()
        `when`(projectDetailsModel.submitUpdateTaskDetails(Task("Existing"), Task("Updated"))).thenReturn(Single.just(listOf(Task("Updated"))))

        //  Act
        projectDetailsPresenter.submitUpdateTaskDetails(Task("Existing"), Task("Updated"))

        //  Assert
        verify(projectDetailsView).displayTasks(listOf(Task("Updated")))
    }

    @Test
    fun shouldShowErrorDuringUpdateTask() {
        //  Arrange
        projectDetailsPresenter.start()
        `when`(projectDetailsModel.submitUpdateTaskDetails(Task("Existing"), Task(""))).thenThrow(ApplicationError("Missing Description"))

        //  Act
        projectDetailsPresenter.submitUpdateTaskDetails(Task("Existing"), Task(""))

        //  Assert
        verify(projectDetailsView).showError(ApplicationError("Missing Description"))
    }

    @Test
    fun shouldShowErrorDuringUpdateTaskProcessing() {
        //  Arrange
        projectDetailsPresenter.start()
        `when`(projectDetailsModel.submitUpdateTaskDetails(Task("Existing"), Task("fasdfasdf"))).thenReturn(Single.error(RuntimeException("Oh shit")))

        //  Act
        projectDetailsPresenter.submitUpdateTaskDetails(Task("Existing"), Task("fasdfasdf"))

        //  Assert
        verify(projectDetailsView).showError(ApplicationError("Unknown error occurred"))
    }

    @Test
    fun shouldShowAppErrorDuringUpdateTaskProcessing() {
        //  Arrange
        projectDetailsPresenter.start()
        `when`(projectDetailsModel.submitUpdateTaskDetails(Task("Existing"), Task("fasdfasdf"))).thenReturn(Single.error(ApplicationError("Oh shit")))

        //  Act
        projectDetailsPresenter.submitUpdateTaskDetails(Task("Existing"), Task("fasdfasdf"))

        //  Assert
        verify(projectDetailsView).showError(ApplicationError("Oh shit"))
    }

    @Test
    fun shouldAllowEditOfProjectDescription() {
        //  Arrange
        projectDetailsPresenter.start()
        `when`(projectDetailsModel.submitUpdatedProjectDetails("Update description")).thenReturn(Single.just(Project("Title", "Update description", ArrayList<Task>())))

        //  Act
        projectDetailsPresenter.submitUpdatedProjectDetails("Update description")

        //  assert
        verify(projectDetailsView).showDescription("Update description")
    }

    @Test
    fun shouldShowErrorIfProjectDescriptionBlankOnEditProject() {
        //  Arrange
        projectDetailsPresenter.start()
        `when`(projectDetailsModel.submitUpdatedProjectDetails("")).thenThrow(ApplicationError("Missing project description"))

        //  Act
        projectDetailsPresenter.submitUpdatedProjectDetails("")

        //  Assert
        verify(projectDetailsView).showError(ApplicationError("Missing project description"))
    }

    @Test
    fun shouldShowErrorIfErrorOccursDuringUpdateProjectDetails() {
        //  Arrange
        projectDetailsPresenter.start()
        `when`(projectDetailsModel.submitUpdatedProjectDetails("")).thenReturn(Single.error(ApplicationError("Missing project description")))

        //  Act
        projectDetailsPresenter.submitUpdatedProjectDetails("")

        //  Assert
        verify(projectDetailsView).showError(ApplicationError("Missing project description"))
    }

    @Test
    fun shouldNotifyModelOfItemPositionChange() {
        //  Arrange
        projectDetailsPresenter.start()
        `when`(projectDetailsModel.updateItemPosition(0, 1)).thenReturn(Completable.complete())

        //  Act
        projectDetailsPresenter.notifyItemMoved(0, 1)

        //  Assert
        verify(projectDetailsModel).updateItemPosition(0, 1)
    }

    @Test
    fun shouldRaiseErrorIfMoveFails() {
        //  Arrange
        projectDetailsPresenter.start()
        `when`(projectDetailsModel.updateItemPosition(0, 1)).thenReturn(Completable.error(ApplicationError("An unknown error occurred")))

        //  Act
        projectDetailsPresenter.notifyItemMoved(0, 1)

        //  Assert
        verify(projectDetailsView).showError(ApplicationError("An unknown error occurred"))
    }

}