package com.vandenbreemen.secretcamera

import android.support.test.espresso.IdlingPolicies
import android.support.test.espresso.IdlingRegistry
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotExist
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo
import com.vandenbreemen.secretcamera.util.ElapsedTimeIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.util.concurrent.TimeUnit

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MainActivityTest {

    companion object {
        val DEFAULT_LOCATION = "Music"
        val TIME_TO_WAIT = TimeUnit.MILLISECONDS.convert(2, TimeUnit.SECONDS)
    }

    val activityRule:ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    var waitResource: ElapsedTimeIdlingResource? = null

    lateinit var fileName:String

    @Before
    fun setup(){
        fileName = "unitTestFile"
        IdlingPolicies.setMasterPolicyTimeout(60, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(26, TimeUnit.SECONDS);
    }

    fun getElapsedTimeIdlingResource(): ElapsedTimeIdlingResource {
        waitResource = ElapsedTimeIdlingResource(TIME_TO_WAIT)
        return waitResource!!
    }

    @After
    fun tearDown(){
        waitResource?.let {
            IdlingRegistry.getInstance().unregister(it)
        }
    }

    @Test
    fun shouldShowFileActionsPrompt(){
        activityRule.launchActivity(null)
        assertDisplayed(R.id.sfsNavFrag)
    }

    @Test
    fun shouldNotShowSFSNavAfterCreating(){

        //  Arrange
        activityRule.launchActivity(null)

        //  Act
        doStandardCreate(null)

        //  Assert
        assertNotExist(R.id.sfsNavFrag)
    }

    @Test
    fun shouldShowTakePictureAfterOpening(){
        //  Arrange
        activityRule.launchActivity(null)

        //  Act
        clickOn(R.id.loadExisting)

        clickOn(DEFAULT_LOCATION)
        clickOn(fileName)
        clickOn(R.id.ok)
        writeTo(R.id.password, "password")
        clickOn(R.id.ok)

        IdlingRegistry.getInstance().register(getElapsedTimeIdlingResource())

        //  Assert
        assertDisplayed(R.id.takePicture)
    }

    @Test
    fun shouldShowTakePictureAfterCreating(){
        //  Arrange
        activityRule.launchActivity(null)

        //  Act
        doStandardCreate(null)

        //  Assert
        assertDisplayed(R.id.takePicture)
    }

    private fun doStandardCreate(theFileName:String?) {
        clickOn(R.id.createNew)
        clickOn(DEFAULT_LOCATION)

        clickOn(R.id.ok)
        writeTo(R.id.fileName, fileName)
        writeTo(R.id.password, "password")
        writeTo(R.id.confirmPassword, "password")

        clickOn(R.id.ok)
    }

}