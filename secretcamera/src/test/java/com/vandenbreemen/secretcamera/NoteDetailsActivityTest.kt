package com.vandenbreemen.secretcamera

import android.content.Intent
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.secretcamera.robot.NoteDetailsRobot
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowApplication

/**
 * Created by kevin on 22/03/18.
 */
@RunWith(RobolectricTestRunner::class)
class NoteDetailsActivityTest {

    lateinit var sfsCredentials:SFSCredentials

    lateinit var sfs:SecureFileSystem

    lateinit var intent:Intent

    @Before
    fun setup(){

        RxJavaPlugins.setComputationSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }

        val file = createTempFile("note_test")
        sfsCredentials = SFSCredentials(file, SecureFileSystem.generatePassword(SecureString.fromPassword("test")))
        sfs = object : SecureFileSystem(sfsCredentials.fileLocation){
            override fun getPassword(): SecureString {
                return sfsCredentials.password
            }
        }

        intent = Intent(ShadowApplication.getInstance().applicationContext, NoteDetailsActivity::class.java)
    }

    @Test
    fun shouldLoadNoteContent(){

        NoteDetailsRobot().apply {
            createNote("test note", "note content")
            val activity = startActivity()
            checkTitle("test note")
            checkContent("note content")
        }
    }

    @Test
    fun shouldReturnToMainOnOkay(){

        NoteDetailsRobot().apply {
            createNote("test note", "note content")
            val activity = startActivity()
            clickOkay(activity)
            checkWentToActivity(activity, MainActivity::class.java)
        }

    }

}