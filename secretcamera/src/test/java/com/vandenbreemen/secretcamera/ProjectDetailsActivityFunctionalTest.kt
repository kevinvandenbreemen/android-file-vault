package com.vandenbreemen.secretcamera

import android.content.Intent
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.FileMeta
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.mobilesecurestorage.security.crypto.setFileMetadata
import com.vandenbreemen.secretcamera.R.id.projectDescription
import com.vandenbreemen.secretcamera.R.id.projectName
import com.vandenbreemen.secretcamera.api.Project
import com.vandenbreemen.secretcamera.api.Task
import com.vandenbreemen.secretcamera.mvp.impl.projects.ProjectFileTypes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import junit.framework.Assert
import junit.framework.TestCase
import junit.framework.TestCase.assertNotNull
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowDialog
import org.robolectric.shadows.ShadowToast

/**
 * @author kevin
 */
@RunWith(RobolectricTestRunner::class)
class ProjectDetailsActivityFunctionalTest {

    lateinit var sfsCredentials: SFSCredentials

    lateinit var sfs: SecureFileSystem

    lateinit var intent: Intent

    lateinit var project: Project

    @Before
    fun setup() {
        RxJavaPlugins.setComputationSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }

        val file = createTempFile("note_test")
        sfsCredentials = SFSCredentials(file, SecureFileSystem.generatePassword(SecureString.fromPassword("test")))
        sfs = object : SecureFileSystem(sfsCredentials.fileLocation) {
            override fun getPassword(): SecureString {
                return sfsCredentials.password
            }
        }

        project = Project("Project Detail Test", "Project details functional test")
        sfs.storeObject(project.title, project)
        sfs.setFileMetadata(project.title, FileMeta(ProjectFileTypes.PROJECT))

        intent = Intent(ApplicationProvider.getApplicationContext(), ProjectDetailsActivity::class.java)
        intent.putExtra(SFSCredentials.PARM_CREDENTIALS, sfsCredentials)
        intent.putExtra(ProjectDetailsActivity.PARM_PROJECT_NAME, "Project Detail Test")

    }

    @Test
    fun projectDescriptionSectionShouldShowProjectDescription() {

        //  Act
        val activity = Robolectric.buildActivity(ProjectDetailsActivity::class.java, intent).create().resume().get()

        //  Assert
        assertEquals(project.details, activity.findViewById<TextView>(projectDescription).text.toString())

    }

    @Test
    fun projectNameShouldShowOnScreen() {
        //  Act
        val activity = Robolectric.buildActivity(ProjectDetailsActivity::class.java, intent).create().resume().get()

        //  Assert
        assertEquals(project.title, activity.findViewById<TextView>(projectName).text.toString())
    }

    @Test
    fun shouldShowTaskListOnInitialLoad() {
        //  Arrange
        project.tasks.add(Task("Test Task 1"))
        project.tasks.add(Task("Test Task 2"))
        sfs.storeObject(project.title, project)

        //  Act
        val activity = Robolectric.buildActivity(ProjectDetailsActivity::class.java, intent).create().resume().get()
        val recyclerView = activity.findViewById<RecyclerView>(R.id.taskList)


        //  Robolectric - force ui thread runs
        //flushBackgroundThreadScheduler()

        //  Hack - force a redraw
        recyclerView.measure(0, 0)
        recyclerView.layout(0, 0, 100, 1000)

        //  Assert
        assertEquals(2, recyclerView.childCount)

    }

    @Test
    fun shouldShowErrorDuringAddTask() {
        //  Arrange
        val activity = Robolectric.buildActivity(ProjectDetailsActivity::class.java, intent).create().resume().get()
        activity.findViewById<FloatingActionButton>(R.id.addTask).performClick()

        //  Act
        ShadowDialog.getLatestDialog().findViewById<Button>(R.id.ok).performClick()

        //  Assert
        assertNotNull("Toast", ShadowToast.getLatestToast())
    }

    @Test
    fun shouldClosePresenterOnPause() {
        val activity = Robolectric.buildActivity(ProjectDetailsActivity::class.java, intent).create().resume().pause().get()

        TestCase.assertTrue(activity.presenter.isClosed())
        Assert.assertEquals(View.VISIBLE, activity.findViewById<ViewGroup>(R.id.overlay).visibility)
    }

}