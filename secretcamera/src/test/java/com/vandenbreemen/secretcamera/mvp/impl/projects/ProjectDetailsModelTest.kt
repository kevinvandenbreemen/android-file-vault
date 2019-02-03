package com.vandenbreemen.secretcamera.mvp.impl.projects

import android.os.Environment
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.FileMeta
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.mobilesecurestorage.security.crypto.setFileMetadata
import com.vandenbreemen.secretcamera.api.Project
import com.vandenbreemen.secretcamera.api.Task
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import junit.framework.TestCase.fail
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class ProjectDetailsModelTest {

    lateinit var credentials: SFSCredentials

    lateinit var sfs: SecureFileSystem

    lateinit var model: ProjectDetailsModel

    val project = Project("Unit Test", "Validation of project detail model")

    @Before
    fun setup() {
        RxJavaPlugins.setComputationSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }

        val sfsFile = File(Environment.getExternalStorageDirectory().toString() + File.separator + "test")
        val tempPassword = "password"
        val testPassword = SecureFileSystem.generatePassword(SecureString.fromPassword(tempPassword))
        credentials = SFSCredentials(sfsFile, testPassword)

        sfs = object : SecureFileSystem(credentials.fileLocation){
            override fun getPassword(): SecureString = credentials.password
        }
        sfs.storeObject(project.title, project)
        sfs.setFileMetadata(project.title, FileMeta(fileType = ProjectFileTypes.PROJECT))


        model = ProjectDetailsModel(project.title, credentials)
        model.init().subscribe()
    }

    @Test
    fun shouldGetProjectTitle() {
        assertEquals(project.title, model.getProjectTitle())
    }

    @Test
    fun shouldGetProjectDescription(){
        assertEquals(project.details, model.getDescription())
    }

    @Test
    fun shouldAddTaskToModel() {

        //  Arrange
        val expected = Task("Unit test task")

        //  Act
        val single: Single<List<Task>> = model.addTask(expected)

        //  Assert
        val test = single.test()
        test.assertComplete()
        assertEquals(1, test.values()[0].size)
        assertEquals(expected, test.values()[0][0])

        model.init().subscribe()
        assertEquals(1, model.getTasks().size)
        assertEquals(Task("Unit test task"), model.getTasks()[0])

    }

    @Test
    fun shouldNotAddTaskIfDescriptionIsBlank() {
        //  Arrange
        val task = Task("")

        //  Act/assert
        try {
            model.addTask(task)
            fail("Task description is blank")
        } catch (err: ApplicationError) {
            err.printStackTrace()
        }
    }

    @Test
    fun shouldUpdateTask() {
        //  Arrange
        val task = Task("New Task")
        val addedTask = model.addTask(task).blockingGet()[0]
        val updatedTask = Task("Updated Task Details")

        //  Act
        val updateSubscribe: Single<List<Task>> = model.submitUpdateTaskDetails(addedTask, Task("Updated Task Details"))
        val test = updateSubscribe.test()

        assertEquals(1, test.values()[0].size)
        assertEquals(updatedTask, test.values()[0][0])

        model.init().subscribe()
        assertEquals(1, model.getTasks().size)
        assertEquals(Task("Updated Task Details"), model.getTasks()[0])
    }

    @Test
    fun shouldPreventUpdateTaskToBlank() {
        //  Arrange
        val task = Task("New Task")
        val addedTask = model.addTask(task).blockingGet()[0]
        val updatedTask = Task("")

        //  Act/assert
        try {
            model.submitUpdateTaskDetails(addedTask, updatedTask)
            fail("Missing details")
        } catch (err: ApplicationError) {
            err.printStackTrace()
        }
    }

    @Test
    fun shouldUpdateProjectDescription() {

        //  Arrange
        val test = model.submitUpdatedProjectDetails("Update Project Description").test()

        //  Assert
        assertEquals(Project("Unit Test", "Update Project Description", ArrayList()), test.values()[0])

        model.init().subscribe()
        assertEquals(Project("Unit Test", "Update Project Description", ArrayList()), model.project)

    }

    @Test
    fun shouldPreventUpdateProjectDescriptionToBlank() {
        try {
            model.submitUpdatedProjectDetails("")
            fail("Project description is blank")
        } catch (err: ApplicationError) {
            err.printStackTrace()
        }
    }

}