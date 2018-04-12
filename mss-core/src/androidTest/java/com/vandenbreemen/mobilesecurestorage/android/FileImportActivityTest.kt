package com.vandenbreemen.mobilesecurestorage.android

import android.Manifest
import android.content.Intent
import android.os.Environment
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.vandenbreemen.mobilesecurestorage.MainActivity
import com.vandenbreemen.mobilesecurestorage.android.api.FileWorkflow
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import org.awaitility.Awaitility
import org.awaitility.Awaitility.await
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.security.SecureRandom
import java.util.concurrent.TimeUnit

/**
 * Created by kevin on 09/04/18.
 */
@RunWith(AndroidJUnit4::class)
class FileImportActivityTest {

    companion object {
        const val DIR_NAME = "ATest"
        const val TAG = "FileImportTest"
    }

    @get:Rule
    val permissions = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

    @get:Rule
    val rule = ActivityTestRule(FileImportActivity::class.java, false, false)

    lateinit var testDir: File

    lateinit var sfsFile: File

    fun createPassword(): SecureString {
        return SecureFileSystem.generatePassword(SecureString.fromPassword("password"))
    }

    fun sfs(): SecureFileSystem {
        return object : SecureFileSystem(sfsFile) {
            override fun getPassword(): SecureString {
                return createPassword()
            }

        }
    }

    @Before
    fun setup() {



        this.testDir = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + DIR_NAME)
        var command = "rm -rf ${testDir.absolutePath}"
        Log.d(TAG, "Delete using command $command")
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(command)

        this.sfsFile = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + "sfs")
        command = "rm -rf ${sfsFile.absolutePath}"
        Log.d(TAG, "Delete using command $command")
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(command)


        Awaitility.await().atMost(30, TimeUnit.SECONDS).until { !testDir.exists() && !sfsFile.exists() }

        Log.d(TAG, "Any previous test data has been cleaned.  Proceeding to test.")

        sfs()
        testDir.mkdir()

        for (i in 1..5) {
            val file = File(testDir.absolutePath + File.separator + "testFile_$i")
            file.createNewFile()
            val bytes = ByteArray(1000000)
            SecureRandom().nextBytes(bytes)
            ObjectOutputStream(FileOutputStream(file)).use {
                it.writeObject(bytes)
            }
        }
    }

    @Test
    fun shouldImportTheFiles() {
        val workflow = FileWorkflow()
        workflow.fileOrDirectory = this.testDir

        val intent = Intent()
        intent.putExtra(FileWorkflow.PARM_WORKFLOW_NAME, workflow)
        intent.putExtra(SFSCredentials.PARM_CREDENTIALS, SFSCredentials(sfsFile, createPassword()))

        rule.launchActivity(intent)

        await().atMost(30, TimeUnit.SECONDS).until {
            Log.d(TAG, "Number of files in SFS:  ${sfs().listFiles().size}")
            sfs().listFiles().size == 5
        }
    }

    @Test
    fun shouldToToDesiredActivityOnComplete(){

        Intents.init()

        val workflow = FileWorkflow()
        workflow.fileOrDirectory = this.testDir
        workflow.activityToStartAfterTargetActivityFinished = MainActivity::class.java

        val intent = Intent()
        intent.putExtra(FileWorkflow.PARM_WORKFLOW_NAME, workflow)
        intent.putExtra(SFSCredentials.PARM_CREDENTIALS, SFSCredentials(sfsFile, createPassword()))

        rule.launchActivity(intent)

        await().atMost(30, TimeUnit.SECONDS).until {
            Log.d(TAG, "Number of files in SFS:  ${sfs().listFiles().size}")
            sfs().listFiles().size == 5
        }

        //intended(hasComponent(ComponentName(getTargetContext(), MainActivity::class.java)))
        intended(hasComponent(MainActivity::class.java.name))
    }

    @Test
    fun shouldBeAbleToImportLotsAndLotsOfFiles() {
        for (i in 1..100) {  //  Import at least 100 files
            val file = File(testDir.absolutePath + File.separator + "testFileEvenMore_$i")
            file.createNewFile()
            val bytes = ByteArray(2000000)
            SecureRandom().nextBytes(bytes)
            ObjectOutputStream(FileOutputStream(file)).use {
                it.writeObject(bytes)
            }
        }

        Awaitility.await().atMost(30, TimeUnit.SECONDS).until { testDir.listFiles().size == 105 }

        val workflow = FileWorkflow()
        workflow.fileOrDirectory = this.testDir

        val intent = Intent()
        intent.putExtra(FileWorkflow.PARM_WORKFLOW_NAME, workflow)
        intent.putExtra(SFSCredentials.PARM_CREDENTIALS, SFSCredentials(sfsFile, createPassword()))

        rule.launchActivity(intent)

        await().atMost(30, TimeUnit.SECONDS).until {
            Log.d(TAG, "Number of files in SFS:  ${sfs().listFiles().size}")
            sfs().listFiles().size == 105
        }
    }

}