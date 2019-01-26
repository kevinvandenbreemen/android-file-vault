package com.vandenbreemen.secretcamera.mvp.impl.projects

import android.os.Environment
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.FileMeta
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.mobilesecurestorage.security.crypto.setFileMetadata
import com.vandenbreemen.secretcamera.api.Project
import com.vandenbreemen.secretcamera.api.Task
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
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

    }

}