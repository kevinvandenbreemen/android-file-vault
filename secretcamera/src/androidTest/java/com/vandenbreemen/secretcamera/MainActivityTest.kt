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
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    val activityRule:ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    lateinit var fileName:String

    @Before
    fun setup(){
        fileName = "testFile_"+System.currentTimeMillis()
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
        doStandardCreate()

        //  Assert
        assertNotExist(R.id.sfsNavFrag)
    }

    @Test
    fun shouldShowTakePictureAfterCreating(){
        //  Arrange
        activityRule.launchActivity(null)

        //  Act
        doStandardCreate()

        //  Assert
        assertDisplayed(R.id.takePicture)
    }

    private fun doStandardCreate() {
        clickOn(R.id.createNew)
        clickOn("Download")

        clickOn(R.id.ok)
        writeTo(R.id.fileName, fileName)
        writeTo(R.id.password, "password")
        writeTo(R.id.confirmPassword, "password")

        clickOn(R.id.ok)
    }

}