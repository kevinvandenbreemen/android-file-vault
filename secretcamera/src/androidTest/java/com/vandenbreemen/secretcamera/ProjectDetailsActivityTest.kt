package com.vandenbreemen.secretcamera

import android.content.Intent
import android.os.Environment
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.IdlingPolicies
import android.support.test.espresso.IdlingRegistry
import android.support.test.rule.ActivityTestRule
import android.util.Log
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.secretcamera.api.Project
import com.vandenbreemen.secretcamera.mvp.impl.projects.ProjectListModel
import com.vandenbreemen.secretcamera.util.ElapsedTimeIdlingResource
import org.awaitility.Awaitility
import org.junit.After
import org.junit.Before
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

    @Before
    fun setup() {

        fileName = "projectDetailTest"
        IdlingPolicies.setMasterPolicyTimeout(60, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(26, TimeUnit.SECONDS);

        this.sfsFile = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + GalleryTest.TESTDIR + File.separator + fileName)
        //  Set up secure file system
        val sfs = object : SecureFileSystem(sfsFile) {
            override fun getPassword(): SecureString {
                return SecureFileSystem.generatePassword(SecureString.fromPassword(GalleryTest.PASSWORD))
            }

        }

        sfsCredentials = SFSCredentials(sfsFile, SecureFileSystem.generatePassword(SecureString.fromPassword(GalleryTest.PASSWORD)))

        project = Project("UI Test Project", "Project of validating that the project details screen works as expected")
        ProjectListModel(sfsCredentials).addNewProject(project)


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
    fun shouldShowProjectDetailsOnClickOfActionsButton() {

        //  Act
        clickOn(R.id.actionsButton)

        //  Assert
        assertDisplayed(R.id.projectDescription)

    }


}