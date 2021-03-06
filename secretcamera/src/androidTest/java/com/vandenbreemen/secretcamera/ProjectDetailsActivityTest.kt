package com.vandenbreemen.secretcamera

import android.Manifest
import android.content.Intent
import android.os.Environment
import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains
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
    fun shouldShowProjectDetailsDialogWhenClickingProjectDescription() {

        //  Arrange
        activityRule.activity.runOnUiThread {
            activityRule.activity.findViewById<View>(R.id.actionsButton).performClick()
        }
        Thread.sleep(500)

        //  Act
        clickOn(R.id.projectDescription)

        //  Assert
        onView(withId(R.id.projectDetails)).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.projectDescriptionForEdit))).check(matches(withText(project.details)))

    }

    @Test
    fun shouldEditProjectDetailsProjectDescription() {
        //  Arrange
        activityRule.activity.runOnUiThread {
            activityRule.activity.findViewById<View>(R.id.actionsButton).performClick()
        }
        Thread.sleep(500)

        clickOn(R.id.projectDescription)

        //  Act
        writeTo(R.id.projectDescriptionForEdit, "Update the project's details")
        onView(allOf(withParent(withId(R.id.projectDetails)), withId(R.id.ok))).perform(click())

        //  Assert
        assertNotExist(R.id.projectDetails)
        onView(withId(R.id.projectDescription)).check(matches(withText("Update the project's details")))

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
        assertEquals(1, activityRule.activity.findViewById<RecyclerView>(R.id.taskList).adapter?.itemCount)
        assertNotExist(R.id.taskDetails)
    }

    @Test
    fun shouldViewTaskInProject() {
        //  Arrange
        clickOn(R.id.addTask)
        writeTo(R.id.taskDescription, "This is a test Task")
        onView(allOf(withParent(withId(R.id.taskDetails)), withId(R.id.ok))).perform(click())

        //  Act
        onView(withId(R.id.taskList)).perform(RecyclerViewActions.actionOnItemAtPosition<TaskViewHolder>(0, click()))

        //  Assert
        assertDisplayed(R.id.taskDetails)
        assertContains(R.id.taskDescription, "This is a test Task")
    }

    @Test
    fun shouldEditTaskInProject() {
        //  Arrange
        clickOn(R.id.addTask)
        writeTo(R.id.taskDescription, "This is a test Task")
        onView(allOf(withParent(withId(R.id.taskDetails)), withId(R.id.ok))).perform(click())
        onView(withId(R.id.taskList)).perform(RecyclerViewActions.actionOnItemAtPosition<TaskViewHolder>(0, click()))

        //  Act
        writeTo(R.id.taskDescription, "Update Task Description")
        onView(allOf(withParent(withId(R.id.taskDetails)), withId(R.id.ok))).perform(click())

        //  Assert
        assertEquals(1, activityRule.activity.findViewById<RecyclerView>(R.id.taskList).adapter?.itemCount)
        onView(withId(R.id.taskList)).perform(RecyclerViewActions.actionOnItemAtPosition<TaskViewHolder>(0, click()))
        assertContains(R.id.taskDescription, "Update Task Description")

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

    @Test
    fun actionsSectionShouldAllowUserToReturnToMain() {

        activityRule.activity.runOnUiThread {
            activityRule.activity.findViewById<View>(R.id.actionsButton).performClick()
        }
        Thread.sleep(500)

        clickOn(R.id.returnToMain)

        assertDisplayed(R.id.addProjectFab)

    }

    @Test
    fun shouldRenameProject() {

        //  Arrange
        clickOn(R.id.titleCard)

        //  Act
        writeTo(R.id.projectNameForEdit, "Update the project's Name")
        onView(allOf(withParent(withId(R.id.projectDetails)), withId(R.id.ok))).perform(click())

        //  Assert
        assertNotExist(R.id.projectNameForEdit)
        onView(withId(R.id.projectName)).check(matches(withText("Update the project's Name")))


    }


}