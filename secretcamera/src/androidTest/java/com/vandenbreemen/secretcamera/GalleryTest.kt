package com.vandenbreemen.secretcamera

import android.os.Environment
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.IdlingPolicies
import android.support.test.espresso.IdlingRegistry
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.vandenbreemen.AppsLoadingIdlingResource
import com.vandenbreemen.mobilesecurestorage.android.CreateSecureFileSystem
import com.vandenbreemen.mobilesecurestorage.android.LoadSecureFileSystem
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.extListFiles
import com.vandenbreemen.mobilesecurestorage.security.crypto.getFileMeta
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.secretcamera.mvp.gallery.PicturesFileTypes
import com.vandenbreemen.secretcamera.util.MainScreenRobot
import com.vandenbreemen.test.BackgroundCompletionCallback
import junit.framework.TestCase.assertEquals
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

    var loadingResource = AppsLoadingIdlingResource()

    lateinit var fileName: String

    lateinit var sfsFile: File

    @Before
    fun setup() {

        //  Arrange
        IdlingRegistry.getInstance().register(loadingResource)
        MainActivity.sfsLoadedCallback = object:BackgroundCompletionCallback {
            override fun onStart() {
                loadingResource.startLoading()
            }

            override fun onFinish() {
                loadingResource.doneLoading()
            }
        }
        LoadSecureFileSystem.sfsLoadedCallback = MainActivity.sfsLoadedCallback
        CreateSecureFileSystem.sfsLoadedCallback = MainActivity.sfsLoadedCallback
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

    @After
    fun tearDown() {
        val command = "rm -rf ${sfsFile.absolutePath}"
        Log.d(TAG, "Delete using command $command")
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(command)
        await().atMost(30, TimeUnit.SECONDS).until { !sfsFile.exists() }
        IdlingRegistry.getInstance().unregister(loadingResource)
    }

    @Test
    fun shouldGoToGalleryFromMain() {
        MainScreenRobot(activityRule.activity).apply {
            loadExistingSFS(TESTDIR, fileName, PASSWORD)
            clickViewPictures().checkOnGalleryScreen()

        }
    }

    @Test
    fun shouldLoadGallery() {
        MainScreenRobot(activityRule.activity).apply {
            loadExistingSFS(TESTDIR, fileName, PASSWORD)
            val robot = clickViewPictures()
            robot.apply galleryRobot@{
                this@galleryRobot.clickViewPictures()
                this@galleryRobot.checkOnPictureViewerScreen()
            }

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

        val sfsForVerification = object : SecureFileSystem(sfsFile) {
            override fun getPassword(): SecureString {
                return SecureFileSystem.generatePassword(SecureString.fromPassword(PASSWORD))
            }

        }
        assertEquals("Files imported", 5, sfsForVerification.extListFiles().size)
        sfsForVerification.extListFiles().forEach({ file ->
            run {
                assertEquals("File type", PicturesFileTypes.IMPORTED_IMAGE, sfsForVerification.getFileMeta(file)?.getFileType())
            }
        })
    }

}