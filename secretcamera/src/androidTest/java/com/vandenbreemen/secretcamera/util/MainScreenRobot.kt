package com.vandenbreemen.secretcamera.util

import android.support.test.espresso.IdlingPolicies
import android.support.test.espresso.IdlingRegistry
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotExist
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo
import com.vandenbreemen.secretcamera.MainActivityTest
import com.vandenbreemen.secretcamera.R
import java.util.concurrent.TimeUnit

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
class MainScreenRobot {

    companion object {
        val TIME_TO_WAIT = TimeUnit.MILLISECONDS.convert(2, TimeUnit.SECONDS)
    }

    val fileName = "unitTestFile"

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
        clickOn(MainActivityTest.DEFAULT_LOCATION)

        clickOn(R.id.ok)
        writeTo(R.id.fileName, fileName)
        writeTo(R.id.password, "password")
        writeTo(R.id.confirmPassword, "password")

        clickOn(R.id.ok)
    }

    fun loadExistingSFS() {
        clickOn(R.id.loadExisting)

        clickOn(MainActivityTest.DEFAULT_LOCATION)
        clickOn(fileName)
        clickOn(R.id.ok)
        writeTo(R.id.password, "password")
        clickOn(R.id.ok)

        IdlingRegistry.getInstance().register(getElapsedTimeIdlingResource())
    }

    fun checkTakePictureDisplayed() = assertDisplayed(R.id.takePicture)

    fun checkNavigationNotDisplayed() = assertNotExist(R.id.sfsNavFrag)

    fun checkTakeNoteDisplayed() = assertDisplayed(R.id.takeNote)

    fun checkNavigationDisplayed() = assertDisplayed(R.id.sfsNavFrag)

}