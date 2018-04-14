package com.vandenbreemen.secretcamera

import android.os.Environment
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.IdlingPolicies
import android.support.test.espresso.IdlingRegistry
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.secretcamera.util.ElapsedTimeIdlingResource
import com.vandenbreemen.secretcamera.util.MainScreenRobot
import org.awaitility.Awaitility.await
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.security.SecureRandom
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class GalleryTest {

    companion object {
        const val TESTDIR = "Music"
        const val IMPORTDIR = "Podcasts"
        const val PASSWORD = "password"
        const val TAG = "GalleryTest"
    }

    val activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    var waitResource: ElapsedTimeIdlingResource? = null

    lateinit var fileName: String

    lateinit var sfsFile: File

    @Before
    fun setup() {

        //  Arrange
        activityRule.launchActivity(null)


        fileName = "galleryTest"
        IdlingPolicies.setMasterPolicyTimeout(60, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(26, TimeUnit.SECONDS);

        this.sfsFile = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + TESTDIR + File.separator + fileName)
        //  Set up secure file system
        object : SecureFileSystem(sfsFile) {
            override fun getPassword(): SecureString {
                return SecureFileSystem.generatePassword(SecureString.fromPassword(PASSWORD))
            }

        }

    }

    fun getElapsedTimeIdlingResource(): ElapsedTimeIdlingResource {
        waitResource = ElapsedTimeIdlingResource(MainActivityTest.TIME_TO_WAIT)
        return waitResource!!
    }

    @After
    fun tearDown() {
        val command = "rm -rf ${sfsFile.absolutePath}"
        Log.d(TAG, "Delete using command $command")
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(command)
        await().atMost(30, TimeUnit.SECONDS).until { !sfsFile.exists() }
        waitResource?.let {
            IdlingRegistry.getInstance().unregister(it)
        }
    }

    @Test
    fun shouldGoToGalleryFromMain() {
        MainScreenRobot(activityRule.activity).apply {
            loadExistingSFS(TESTDIR, fileName, PASSWORD)
            clickViewPictures().checkOnGalleryScreen()

        }
    }

    @Test
    fun shouldInitImageImport() {
        MainScreenRobot(activityRule.activity).apply {
            loadExistingSFS(TESTDIR, fileName, PASSWORD)

            val robot = clickViewPictures()
            robot.apply {
                clickImportImages()
                checkOnDirectorySelectScreen()
            }

        }
    }

    @Test
    fun shouldImportImages() {

        //  Stand up a few files to simulate an import
        for (i in 1..5) {
            val file = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + IMPORTDIR + File.separator + "IMG_$i.jpg")
            file.createNewFile()
            val bytes = ByteArray(100000)
            SecureRandom().nextBytes(bytes)
            ObjectOutputStream(FileOutputStream(file)).use {
                it.writeObject(bytes)
            }
        }

        MainScreenRobot(activityRule.activity).apply {
            loadExistingSFS(TESTDIR, fileName, PASSWORD)

            val robot = clickViewPictures()
            robot.apply {
                clickImportImages()
                checkOnDirectorySelectScreen()
                selectDirectory(IMPORTDIR)

                IdlingRegistry.getInstance().register(getElapsedTimeIdlingResource())

                checkOnGalleryScreen()
            }

        }
    }

}