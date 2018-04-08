package com.vandenbreemen.mobilesecurestorage.android

import android.Manifest
import android.os.Environment
import android.support.test.InstrumentationRegistry
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.vandenbreemen.mobilesecurestorage.TestResources
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
@RunWith(AndroidJUnit4::class)
class ADBFunctionalityLearningTest {

    companion object {
        val TAG = "LearningTests"
    }

    //  Set up storage/reading permissions
    @get:Rule
    val permissions = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)


    @Test
    fun testUploadFileToDevice() {

        val localFile = TestResources.TEST_RES_IMG_1
        val command = "push $localFile ${Environment.getExternalStorageDirectory().absolutePath}"
        Log.d(TAG, "Executing command:  $command")
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                command
        )

        //  Now list the files
        val updaloaded = File(Environment.getExternalStorageDirectory().absolutePath + "/" +
                TestResources.FILENAME_RES_IMG_1)
        assertTrue("File uploaded:  ${updaloaded.absolutePath}", updaloaded.exists())
    }

}