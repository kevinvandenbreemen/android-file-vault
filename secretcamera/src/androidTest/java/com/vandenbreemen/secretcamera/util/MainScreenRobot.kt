package com.vandenbreemen.secretcamera.util

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Environment
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.IdlingPolicies
import android.support.test.espresso.IdlingRegistry
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotExist
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo
import com.vandenbreemen.secretcamera.R
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
class MainScreenRobot(val activity: Activity) {

    companion object {
        val TIME_TO_WAIT = TimeUnit.MILLISECONDS.convert(2, TimeUnit.SECONDS)
        val FILENAME = "unitTestFile"
        val DEFAULT_LOCATION = "Music"
    }

    var waitResource: ElapsedTimeIdlingResource? = null

    init {
        IdlingPolicies.setMasterPolicyTimeout(60, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(26, TimeUnit.SECONDS);
    }

    fun getElapsedTimeIdlingResource(): ElapsedTimeIdlingResource {
        waitResource = ElapsedTimeIdlingResource(TIME_TO_WAIT)
        return waitResource!!
    }

    fun createNewSFS() {
        clickOn(R.id.createNew)
        clickOn(DEFAULT_LOCATION)

        clickOn(R.id.ok)
        writeTo(R.id.fileName, FILENAME)
        writeTo(R.id.password, "password")
        writeTo(R.id.confirmPassword, "password")

        clickOn(R.id.ok)
    }

    fun loadExistingSFS() {
        clickOn(R.id.loadExisting)

        clickOn(DEFAULT_LOCATION)
        clickOn(FILENAME)
        clickOn(R.id.ok)
        writeTo(R.id.password, "password")
        clickOn(R.id.ok)

        IdlingRegistry.getInstance().register(getElapsedTimeIdlingResource())
    }

    fun rotateToLandscape() {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    fun deleteTestFile() {
        val dir = Environment.getExternalStorageDirectory()
        val file = File(dir.absolutePath
                + File.separator + DEFAULT_LOCATION
                + File.separator + FILENAME)
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                "rm -f " + file.absolutePath)
    }

    fun checkTakePictureDisplayed() = assertDisplayed(R.id.takePicture)

    fun checkNavigationNotDisplayed() = assertNotExist(R.id.sfsNavFrag)

    fun checkTakeNoteDisplayed() = assertDisplayed(R.id.takeNote)

    fun checkNavigationDisplayed() = assertDisplayed(R.id.sfsNavFrag)
    fun checkNotesDisplayed() = assertDisplayed(R.id.view_notes)

}