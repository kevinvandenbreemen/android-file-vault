package com.vandenbreemen.secretcamera

import android.Manifest
import android.content.Intent
import android.os.Environment
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingPolicies
import android.support.test.espresso.action.ViewActions.replaceText
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withParent
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.secretcamera.api.Project
import com.vandenbreemen.secretcamera.util.ElapsedTimeIdlingResource
import org.hamcrest.CoreMatchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.util.concurrent.TimeUnit

class ProjectsActivityUITest {

    var waitResource: ElapsedTimeIdlingResource? = null

    lateinit var fileName: String

    lateinit var sfsFile: File

    lateinit var project: Project

    lateinit var sfsCredentials: SFSCredentials

    val activityRule: ActivityTestRule<ProjectsActivity> = ActivityTestRule(ProjectsActivity::class.java)

    @get:Rule
    val permissions = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

    @Before
    fun setup() {

        fileName = "projectListTest"
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


        //  Fire up the activity now
        val intent = Intent(InstrumentationRegistry.getTargetContext(), ProjectsActivity::class.java)
        intent.putExtra(SFSCredentials.PARM_CREDENTIALS, sfsCredentials)
        activityRule.launchActivity(intent)
    }

    @Test
    fun shouldAddNewProject() {

        clickOn(R.id.addProjectFab)

        onView(allOf(withParent(withId(R.id.addProjectDetails)), withId(R.id.projectName)))
                .perform(replaceText("Test Project"))
        onView(allOf(withParent(withId(R.id.addProjectDetails)), withId(R.id.projectDescription)))
                .perform(replaceText("Description of the test project"))

        clickOn(R.id.ok)

        //  Assert
        assertNotDisplayed(R.id.addProjectDialog)


    }

}