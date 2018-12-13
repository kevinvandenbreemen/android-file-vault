package com.vandenbreemen.secretcamera.mvp.impl.projects

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.api.Project
import com.vandenbreemen.secretcamera.mvp.projects.ProjectListPresenter
import com.vandenbreemen.secretcamera.mvp.projects.ProjectListRouter
import com.vandenbreemen.secretcamera.mvp.projects.ProjectListView
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ProjectListPresenterTest {

    @Mock
    lateinit var view: ProjectListView

    @Mock
    lateinit var model: ProjectListModel

    @Mock
    lateinit var router: ProjectListRouter

    lateinit var presenter: ProjectListPresenter

    @Before
    fun setup(){

        `when`(model.init()).thenReturn(Single.just(Unit))

        //  Arrange
        `when`(model.getProjects()).thenReturn(Single.just(
                listOf(
                        Project("Project 1", "The first Project"),
                        Project("Project 2", "The second Project")
                )
        ))

        presenter = ProjectListPresenterImpl(model, view, router)
    }

    @Test
    fun shouldDisplayProjectsOnStart(){

        //  Act
        presenter.start()

        //  Assert
        verify(view).showProjects(listOf(
                Project("Project 1", "The first Project"),
                Project("Project 2", "The second Project")
        ))
    }

    @Test
    fun shouldTakeInNewProject() {

        //  Arrange

        `when`(model.addNewProject(Project("New Project", "Test adding a new project"))).thenReturn(Completable.complete())

        presenter.start()

        `when`(model.getProjects()).thenReturn(Single.just(
                listOf(
                        Project("Project 1", "The first Project"),
                        Project("Project 2", "The second Project"),
                        Project("New Project", "Test adding a new project")
                )
        ))

        //  Act
        presenter.addProject(Project("New Project", "Test adding a new project"))

        //  Assert
        verify(model).addNewProject(Project("New Project", "Test adding a new project"))
        verify(view).showProjects(
                listOf(
                        Project("Project 1", "The first Project"),
                        Project("Project 2", "The second Project"),
                        Project("New Project", "Test adding a new project")
                )
        )

    }

    @Test
    fun shouldDisplayErrorOnAddNewProject() {

        //  Arrange
        `when`(model.addNewProject(Project("New Project", "Test adding a new project"))).thenReturn(Completable.error(
                ApplicationError("Failed to add Project"))
        )

        presenter.start()

        //  Act
        presenter.addProject(Project("New Project", "Test adding a new project"))

        //  Assert
        verify(view).showError(ApplicationError("Failed to add Project"))

    }

    @Test
    fun shouldViewProjectDetails() {

        //  Arrange
        val project = Project("New Project", "Details on the new Project")
        val credentials = mock(SFSCredentials::class.java)
        `when`(model.copyCredentials()).thenReturn(credentials)

        //  Act
        presenter.viewProjectDetails(project)

        //  Assert
        verify(router).gotoProjectDetails("New Project", credentials)
    }

}