package com.vandenbreemen.secretcamera.mvp.impl.projects

import android.os.Environment
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.FileMeta
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.listFiles
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.mobilesecurestorage.security.crypto.setFileMetadata
import com.vandenbreemen.secretcamera.api.Project
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
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
        val testObserver = model.addNewProject(newProject).test()

        //  Assert
        assertEquals(0, testObserver.errorCount())
        testObserver.assertComplete()

        val forVerification = object : SecureFileSystem(credentials.fileLocation){
            override fun getPassword(): SecureString {
                return credentials.password
            }
        }
        assertTrue(forVerification.exists("Test Project"))
        assertEquals(1, forVerification.listFiles(ProjectFileTypes.PROJECT).size)
    }

    @Test
    fun shouldGetProjectList() {
        //  Arrange
        model.addNewProject(Project("Project 1", "First Project")).subscribe()
        model.addNewProject(Project("Project 2", "Second Project")).subscribe()

        //  Act
        val testObserver = model.getProjects().test()

        //  Assert
        testObserver.assertNoErrors()
        testObserver.assertResult(
                listOf(
                        Project("Project 1", "First Project"),
                        Project("Project 2", "Second Project")
                )
        )
    }

    @Test
    fun shouldPreventAddingProjectWithSameTitleAsExistingProject() {

        //  Arrange

        sfs.storeObject("tEsT project", Project("other Projet", "Some other project"))
        sfs.setFileMetadata("tEsT project", FileMeta(fileType = ProjectFileTypes.PROJECT))
        model = ProjectListModel(credentials)
        model.init().subscribe()

        val newProject = Project("Test Project", "This is a test description of the project")

        //  Act
        assertEquals(1, model.addNewProject(newProject).test().errorCount())

        //  Assert
        val forVerification = object : SecureFileSystem(credentials.fileLocation){
            override fun getPassword(): SecureString {
                return credentials.password
            }
        }

        assertEquals(1, forVerification.listFiles(ProjectFileTypes.PROJECT).size)
    }

    @Test
    fun shouldPreventAddingProjectWithNoTitle() {

        //  Arrange
        val completable = model.addNewProject(Project("", "Blabkab"))

        //  Act
        val test = completable.test()

        //  Assert
        test.assertNotComplete()
        assertEquals(1, test.errorCount())

        val forVerification = object : SecureFileSystem(credentials.fileLocation){
            override fun getPassword(): SecureString {
                return credentials.password
            }
        }

        assertEquals(0, forVerification.listFiles(ProjectFileTypes.PROJECT).size)
    }

}