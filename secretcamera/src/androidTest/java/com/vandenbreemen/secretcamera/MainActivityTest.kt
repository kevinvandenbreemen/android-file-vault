package com.vandenbreemen.secretcamera

import android.support.test.espresso.IdlingPolicies
import android.support.test.espresso.IdlingRegistry
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.vandenbreemen.secretcamera.util.ElapsedTimeIdlingResource
import com.vandenbreemen.secretcamera.util.MainScreenRobot
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

        val TIME_TO_WAIT = TimeUnit.MILLISECONDS.convert(2, TimeUnit.SECONDS)
    }

    val activityRule:ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    var waitResource: ElapsedTimeIdlingResource? = null

    lateinit var fileName:String

    @Before
    fun setup(){

        //  Arrange
        activityRule.launchActivity(null)

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
        MainScreenRobot(activityRule.activity).apply {
            checkNavigationDisplayed()
        }
    }

    @Test
    fun shouldNotShowSFSNavAfterCreating(){


        MainScreenRobot(activityRule.activity).apply {
            deleteTestFile()
            createNewSFS()
            //  Assert
            checkNavigationNotDisplayed()
        }

    }

    @Test
    fun shouldShowTakePictureAfterOpening(){

        MainScreenRobot(activityRule.activity).apply {
            loadExistingSFS()
            checkTakePictureDisplayed()
        }
    }

    @Test
    fun shouldPreserveActionsAfterOpeningAndReorienting() {

        MainScreenRobot(activityRule.activity).apply {
            loadExistingSFS()
            rotateToLandscape()
            checkTakeNoteDisplayed()
            checkNotesDisplayed()
            checkTakePictureDisplayed()
        }
    }

    @Test
    fun shouldShowTakeNoteAfterOpening(){

        MainScreenRobot(activityRule.activity).apply {
            loadExistingSFS()
            checkTakeNoteDisplayed()
        }
    }

    @Test
    fun shouldShowTakePictureAfterCreating(){

        MainScreenRobot(activityRule.activity).apply {
            deleteTestFile()
            createNewSFS()
            checkTakePictureDisplayed()
        }
    }

    @Test
    fun shouldStartTakeNoteWorkflow() {
        MainScreenRobot(activityRule.activity).apply {
            deleteTestFile()
            createNewSFS()
            clickTakeNote().apply {
                checkTitleDisplayed()
                checkContentDisplayed()
            }
        }
    }

}