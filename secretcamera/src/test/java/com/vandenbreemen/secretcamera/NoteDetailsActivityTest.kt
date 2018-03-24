package com.vandenbreemen.secretcamera

import android.content.Intent
import android.widget.EditText
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.extListFiles
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.secretcamera.mvp.impl.TakeNewNoteModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowApplication
import java.io.File

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

        RxJavaPlugins.setIoSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }

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
        TakeNewNoteModel.storeNote(sfs, "test note", "note content")
        val noteFile = sfs.extListFiles()[0]
        val selection = StringSelection(noteFile, sfsCredentials)

        intent.putExtra(SELECTED_STRING, selection)
        val activity = Robolectric.buildActivity(NoteDetailsActivity::class.java, intent)
                .create()
                .resume()
                .get()

        assertEquals("Note title", "test note", activity.findViewById<EditText>(R.id.title).text.toString())
        assertEquals("Note content", "note content", activity.findViewById<EditText>(R.id.content).text.toString())
    }

}