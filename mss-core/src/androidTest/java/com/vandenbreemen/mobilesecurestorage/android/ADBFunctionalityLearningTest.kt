package com.vandenbreemen.mobilesecurestorage.android

import android.Manifest
import android.os.Environment
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ErrorCollector
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.io.File

/**
 * <h2>Intro</h2>
 * Trying to figure out how to delete test files:
 * <br/>See https://stackoverflow.com/questions/49363485/how-to-put-test-data-files-on-a-device-for-instrumental-tests
 * <h2>Other Details</h2>
 * @author kevin
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ADBFunctionalityLearningTest {

    companion object {
        val TAG = "LearningTests"
        val testFilename = "testData_${System.currentTimeMillis()}"
    }

    //  Set up storage/reading permissions
    @get:Rule
    val permissions = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

    @get:Rule
    val errorCollector: ErrorCollector = ErrorCollector()

    lateinit var testFilename: String

    @Before
    fun setup() {
        this.testFilename = "testData_${System.currentTimeMillis()}"
    }

    @Test
    fun howToCreateANewFileOnDevice() {
        val newFile = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + testFilename)
        errorCollector.checkThat(newFile.createNewFile(), `is`(true))
        errorCollector.checkThat(newFile.exists(), `is`(true))
    }

    @Test
    fun howToDeleteTestData() {
        val newFile = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + testFilename)
        errorCollector.checkThat(newFile.delete(), `is`(true))  //  Won't delete the file....
        errorCollector.checkThat(newFile.exists(), `is`(false))
    }


}