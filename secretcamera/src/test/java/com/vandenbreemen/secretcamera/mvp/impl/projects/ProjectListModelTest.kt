package com.vandenbreemen.secretcamera.mvp.impl.projects

import android.os.Environment
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.listFiles
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.secretcamera.api.Project
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog
import java.io.File

@RunWith(RobolectricTestRunner::class)
class ProjectListModelTest {

    lateinit var credentials: SFSCredentials

    lateinit var sfs: SecureFileSystem

    lateinit var model: ProjectListModel

    @Before
    fun setup(){
        RxJavaPlugins.setComputationSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }
        ShadowLog.stream = System.out

        val sfsFile = File(Environment.getExternalStorageDirectory().toString() + File.separator + "test")
        val tempPassword = "password"
        val testPassword = SecureFileSystem.generatePassword(SecureString.fromPassword(tempPassword))
        credentials = SFSCredentials(sfsFile, testPassword)

        sfs = object : SecureFileSystem(credentials.fileLocation){
            override fun getPassword(): SecureString = credentials.password
        }

        model = ProjectListModel(credentials)
        model.init().subscribe()
    }

    @Test
    fun shouldAddNewProject(){

        //  Arrange
        val newProject = Project("Test Project", "This is a test description of the project")

        //  Act
        model.addNewProject(newProject).subscribe()

        //  Assert
        val forVerification = object : SecureFileSystem(credentials.fileLocation){
            override fun getPassword(): SecureString {
                return credentials.password
            }
        }
        TestCase.assertTrue(forVerification.exists("Test Project"))
        TestCase.assertEquals(1, forVerification.listFiles(ProjectFileTypes.PROJECT).size)
    }

}