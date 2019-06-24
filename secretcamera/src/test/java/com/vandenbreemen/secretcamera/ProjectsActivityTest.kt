package com.vandenbreemen.secretcamera

import android.content.Intent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.listFiles
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.secretcamera.api.Project
import com.vandenbreemen.secretcamera.mvp.impl.projects.ProjectFileTypes
import com.vandenbreemen.secretcamera.mvp.impl.projects.ProjectListModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import junit.framework.Assert
import junit.framework.TestCase.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
class ProjectsActivityTest {

    lateinit var sfsCredentials:SFSCredentials

    lateinit var sfs:SecureFileSystem

    lateinit var intent: Intent

    @Before
    fun setup() {
        RxJavaPlugins.setComputationSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }

        val file = createTempFile("note_test")
        sfsCredentials = SFSCredentials(file, SecureFileSystem.generatePassword(SecureString.fromPassword("test")))
        sfs = object : SecureFileSystem(sfsCredentials.fileLocation){
            override fun getPassword(): SecureString {
                return sfsCredentials.password
            }
        }

        intent = Intent(ApplicationProvider.getApplicationContext(), ProjectsActivity::class.java)
        intent.putExtra(SFSCredentials.PARM_CREDENTIALS, sfsCredentials)
    }

    @Test
    fun shouldAddProject() {

        //  Arrange
        val activity = buildActivity(ProjectsActivity::class.java, intent).create().resume().get()
        activity.findViewById<FloatingActionButton>(R.id.addProjectFab).performClick()

        //  Act
        assertEquals(View.VISIBLE, activity.findViewById<ViewGroup>(R.id.addProjectDialog).visibility)
        activity.findViewById<EditText>(R.id.projectName).setText("Test Project")
        activity.findViewById<EditText>(R.id.projectDescription).setText("This is a test of adding a project to the system!")
        activity.findViewById<Button>(R.id.ok).performClick()

        //  Assert
        val forVerification = object : SecureFileSystem(sfsCredentials.fileLocation){
            override fun getPassword(): SecureString {
                return sfsCredentials.password
            }
        }
        assertTrue(forVerification.exists("Test Project"))
        assertEquals(1, forVerification.listFiles(ProjectFileTypes.PROJECT).size)

    }

    @Test
    fun shouldRaiseErrorOnNoTitle() {
        //  Arrange
        val activity = buildActivity(ProjectsActivity::class.java, intent).create().resume().get()
        activity.findViewById<FloatingActionButton>(R.id.addProjectFab).performClick()

        //  Act
        activity.findViewById<Button>(R.id.ok).performClick()

        //  Assert
        val toast = ShadowToast.getLatestToast()
        assertNotNull("Toast", toast)
    }

    @Test
    fun shouldDisplayProjectList() {

        //  Arrange
        val tempModel = ProjectListModel(sfsCredentials)
        tempModel.init().subscribe()
        tempModel.addNewProject(Project("Project 1", "The first Project")).subscribe()
        tempModel.addNewProject(Project("Project 2", "The Second Project")).subscribe()

        //  Act
        val activity = buildActivity(ProjectsActivity::class.java, intent).create().resume().get()

        //  Assert
        assertEquals(View.GONE, activity.findViewById<ViewGroup>(R.id.addProjectDialog).visibility)
        val projectList = activity.findViewById<RecyclerView>(R.id.projectList)

        //  Robolectric hack
        projectList.measure(0,0)
        projectList.layout(0,0,100,1000)

        assertEquals("Projects not listed", 2, projectList.adapter?.itemCount)

    }

    @Test
    fun shouldGoToProjectDetailsOnSelectProject() {

        //  Arrange
        val tempModel = ProjectListModel(sfsCredentials)
        tempModel.init().subscribe()
        tempModel.addNewProject(Project("Project 1", "The first Project")).subscribe()
        tempModel.addNewProject(Project("Project 2", "The Second Project")).subscribe()

        //  Act
        val activity = buildActivity(ProjectsActivity::class.java, intent).create().resume().get()
        val projectList = activity.findViewById<RecyclerView>(R.id.projectList)

        //  Robolectric hack
        projectList.measure(0,0)
        projectList.layout(0,0,100,1000)

        projectList.findViewHolderForAdapterPosition(1)?.itemView?.performClick()

        //  Assert
        val nextActivity = shadowOf(activity).nextStartedActivity
        assertNotNull(nextActivity)

        assertEquals(ProjectDetailsActivity::class.java, shadowOf(nextActivity).intentClass)
        assertNotNull(nextActivity.getParcelableExtra(SFSCredentials.PARM_CREDENTIALS))
        assertEquals("Project 2", nextActivity.getStringExtra(ProjectDetailsActivity.PARM_PROJECT_NAME))

    }

    @Test
    fun shouldDestroySFSOnPause() {
        val activity = buildActivity(ProjectsActivity::class.java, intent).create().resume().pause().get()

        assertTrue(activity.presenter.isClosed())
        Assert.assertEquals(VISIBLE, activity.findViewById<ViewGroup>(R.id.overlay).visibility)
    }

    @Test
    fun shouldNotShowOverlayOnStart(){
        //  Arrange
        val activity = buildActivity(ProjectsActivity::class.java, intent).create().resume().get()

        //  Assert
        Assert.assertEquals(GONE, activity.findViewById<ViewGroup>(R.id.overlay).visibility)
    }

}