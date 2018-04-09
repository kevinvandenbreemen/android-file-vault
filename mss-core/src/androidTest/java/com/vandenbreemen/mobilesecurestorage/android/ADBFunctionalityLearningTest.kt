package com.vandenbreemen.mobilesecurestorage.android

import android.Manifest
import android.os.Environment
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import org.awaitility.Awaitility.await
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ErrorCollector
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ADBFunctionalityLearningTest {

    companion object {
        val TAG = "LearningTests"

    }

    //  Set up storage/reading permissions
    @get:Rule
    val permissions = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

    @get:Rule
    val errorCollector: ErrorCollector = ErrorCollector()

    @Before
    fun setup() {
    }

    @Test
    fun howToCreateANewFileOnDevice() {
        val testFilename = "testData_${System.currentTimeMillis()}"
        val newFile = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + testFilename)
        Log.d(TAG, "Try and create ${newFile.absolutePath}")
        errorCollector.checkThat(newFile.createNewFile(), `is`(true))
        errorCollector.checkThat(newFile.exists(), `is`(true))
        getInstrumentation().getUiAutomation().executeShellCommand("rm -f ${newFile.absolutePath}")
    }

    @Test
    fun howToDeleteTestData() {
        val testFilename = "testData_${System.currentTimeMillis()}"
        val newFile = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + testFilename)
        Log.d(TAG, "Try and delete ${newFile.absolutePath}")
        errorCollector.checkThat(newFile.createNewFile(), `is`(true))
        val command = "rm -f ${newFile.absolutePath}"
        Log.d(TAG, "Delete using command $command")
        getInstrumentation().getUiAutomation().executeShellCommand(command)
        await().atMost(5, TimeUnit.SECONDS).until { !newFile.exists() }

        errorCollector.checkThat(newFile.exists(), `is`(false))
    }


}