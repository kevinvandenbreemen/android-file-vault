package com.vandenbreemen.secretcamera

import android.Manifest
import android.content.Intent
import android.os.Environment
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingPolicies
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotExist
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.secretcamera.api.Project
import com.vandenbreemen.secretcamera.mvp.impl.projects.ProjectListModel
import com.vandenbreemen.secretcamera.util.ElapsedTimeIdlingResource
import junit.framework.TestCase.assertEquals
import org.awaitility.Awaitility
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.util.concurrent.TimeUnit

/**
 *
 * @author kevin
 */
class ProjectDetailsActivityTest {

    var waitResource: ElapsedTimeIdlingResource? = null

    lateinit var fileName: String

    lateinit var sfsFile: File

    lateinit var project: Project

    lateinit var sfsCredentials: SFSCredentials

    val activityRule: ActivityTestRule<ProjectDetailsActivity> = ActivityTestRule(ProjectDetailsActivity::class.java)

    @get:Rule
    val permissions = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

    @Before
    fun setup() {

        fileName = "projectDetailTest"
        IdlingPolicies.setMasterPolicyTimeout(60, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(26, TimeUnit.SECONDS);


        this.sfsFile = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + fileName)
        this.sfsFile.createNewFile()
        //  Set up secure file system
        val sfs = object : SecureFileSystem(sfsFile) {
            override fun getPassword(): SecureString {
                return SecureFileSystem.generatePassword(SecureString.fromPassword(GalleryTest.PASSWORD))
            }

        }

        sfsCredentials = SFSCredentials(sfsFile, SecureFileSystem.generatePassword(SecureString.fromPassword(GalleryTest.PASSWORD)))

        project = Project("UI Test Project", "Project of validating that the project details screen works as expected")
        val tempModel = ProjectListModel(sfsCredentials)
        tempModel.init().blockingGet()
        tempModel.addNewProject(project).blockingAwait()



        //  Fire up the activity now
        val intent = Intent(InstrumentationRegistry.getTargetContext(), ProjectDetailsActivity::class.java)
        intent.putExtra(SFSCredentials.PARM_CREDENTIALS, sfsCredentials)
        intent.putExtra(ProjectDetailsActivity.PARM_PROJECT_NAME, "UI Test Project")
        activityRule.launchActivity(intent)
    }

    @After
    fun tearDown() {
        val command = "rm -rf ${sfsFile.absolutePath}"
        Log.d(GalleryTest.TAG, "Delete using command $command")
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(command)
        Awaitility.await().atMost(30, TimeUnit.SECONDS).until { !sfsFile.exists() }
        waitResource?.let {
            IdlingRegistry.getInstance().unregister(it)
        }
    }

    @Test
    fun shouldHideProjectDescriptionOnInitialLoad() {
        //  Assert
        assertNotDisplayed(R.id.projectDescription)
    }

    @Test
    fun shouldShowAddTaskDialogWhenClickingAddTask() {
        //  Act
        clickOn(R.id.addTask)

        //  Assert
        onView(withId(R.id.taskDetails)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldCancelAddTask() {
        //  Arrange
        clickOn(R.id.addTask)

        //  Act
        clickOn(R.id.cancel)

        //  Assert
        assertNotExist(R.id.taskDetails)
    }

    @Test
    fun shouldAddTaskToProject() {
        //  Arrange
        clickOn(R.id.addTask)

        //  Act
        writeTo(R.id.taskDescription, "This is a test Task")
        onView(allOf(withParent(withId(R.id.taskDetails)), withId(R.id.ok))).perform(click())

        //  Assert
        assertEquals(1, activityRule.activity.findViewById<RecyclerView>(R.id.taskList).adapter.itemCount)
        assertNotExist(R.id.taskDetails)
    }

    @Test
    fun shouldShowProjectDetailsOnClickOfActionsButton() {

        //  Act
        activityRule.activity.runOnUiThread {
            activityRule.activity.findViewById<View>(R.id.actionsButton).performClick()
        }
        Thread.sleep(1000)



        //  Assert
        assertDisplayed(R.id.projectDescription)
        assertEquals(
                project.details,
                activityRule.activity.findViewById<TextView>(R.id.projectDescription).text.toString()
        )

    }

    @Test
    fun shouldHideProjectDetailsOnSecondClickOfActionsButton() {
        //  Act
        activityRule.activity.runOnUiThread {
            activityRule.activity.findViewById<View>(R.id.actionsButton).performClick()
        }
        Thread.sleep(500)
        //  Act
        activityRule.activity.runOnUiThread {
            activityRule.activity.findViewById<View>(R.id.actionsButton).performClick()
        }
        Thread.sleep(500)

        assertNotDisplayed(R.id.projectDescription)
    }

    @Test
    fun shouldBeAbleToReUseActionsButtonAfterHidingItAgain() {

        activityRule.activity.runOnUiThread {
            activityRule.activity.findViewById<View>(R.id.actionsButton).performClick()
        }
        Thread.sleep(500)

        activityRule.activity.runOnUiThread {
            activityRule.activity.findViewById<View>(R.id.actionsButton).performClick()
        }
        Thread.sleep(500)

        activityRule.activity.runOnUiThread {
            activityRule.activity.findViewById<View>(R.id.actionsButton).performClick()
        }
        Thread.sleep(500)

        //  Assert
        assertDisplayed(R.id.projectDescription)

    }


}