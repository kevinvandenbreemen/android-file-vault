package com.vandenbreemen.secretcamera

import android.support.test.espresso.IdlingPolicies
import android.support.test.espresso.IdlingRegistry
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.vandenbreemen.secretcamera.util.ElapsedTimeIdlingResource
import com.vandenbreemen.secretcamera.util.MainScreenRobot
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class GalleryTest {

    val activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    var waitResource: ElapsedTimeIdlingResource? = null

    lateinit var fileName: String

    @Before
    fun setup() {

        //  Arrange
        activityRule.launchActivity(null)

        fileName = "unitTestFile"
        IdlingPolicies.setMasterPolicyTimeout(60, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(26, TimeUnit.SECONDS);
    }

    fun getElapsedTimeIdlingResource(): ElapsedTimeIdlingResource {
        waitResource = ElapsedTimeIdlingResource(MainActivityTest.TIME_TO_WAIT)
        return waitResource!!
    }

    @After
    fun tearDown() {
        waitResource?.let {
            IdlingRegistry.getInstance().unregister(it)
        }
    }

    @Test
    fun shouldGoToGalleryFromMain() {
        MainScreenRobot(activityRule.activity).apply {
            createNewSFS()
            clickViewPictures().checkOnGalleryScreen()

        }
    }

}