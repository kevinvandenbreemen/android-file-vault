package com.vandenbreemen.secretcamera

import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotExist
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo
import com.schibsted.spain.barista.interaction.BaristaScrollInteractions.scrollTo
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

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
    }

    val activityRule:ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    lateinit var fileName:String

    @Before
    fun setup(){
        fileName = "unitTestFile"
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