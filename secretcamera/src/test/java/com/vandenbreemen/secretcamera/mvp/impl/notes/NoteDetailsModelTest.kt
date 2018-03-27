package com.vandenbreemen.secretcamera.mvp.impl.notes

import android.os.Environment
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.extListFiles
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.secretcamera.api.Note
import com.vandenbreemen.secretcamera.mvp.impl.TakeNewNoteModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import junit.framework.TestCase.assertNotNull
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ErrorCollector
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog
import java.io.File

/**
 * Created by kevin on 22/03/18.
 */
@RunWith(RobolectricTestRunner::class)
class NoteDetailsModelTest {

    @get:Rule
    val errorCollector:ErrorCollector = ErrorCollector()

    lateinit var sut:NoteDetailsModel

    lateinit var credentials:SFSCredentials

    lateinit var sfs:SecureFileSystem

    @Before
    fun setup(){
        RxJavaPlugins.setComputationSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }
        ShadowLog.stream = System.out

        val sfsFile = File(Environment.getExternalStorageDirectory().toString() + File.separator + "test")
        val tempPassword = "password"
        val testPassword = SecureFileSystem.generatePassword(SecureString.fromPassword(tempPassword))
        credentials = SFSCredentials(sfsFile, testPassword)

        sfs = object : SecureFileSystem(credentials.fileLocation){
            override fun getPassword(): SecureString = credentials.password
        }

    }

    @Test
    fun shouldLoadNote(){
        TakeNewNoteModel.storeNote(sfs, "Test Note", "Test Note Content")
        val noteFile = sfs.extListFiles()[0]
        sut = NoteDetailsModel(credentials, noteFile)
        sut.init().subscribe()

        val note: Note = sut.getNote()
        assertNotNull("Note", note)
        errorCollector.checkThat(note.title, `is`("Test Note"))
        errorCollector.checkThat(note.content, `is`("Test Note Content"))
    }

    @Test
    fun shouldUpdateNote(){
        TakeNewNoteModel.storeNote(sfs, "Test Note", "Test Note Content")
        val noteFile = sfs.extListFiles()[0]
        sut = NoteDetailsModel(credentials, noteFile)
        sut.init().subscribe()

        val single:Single<Unit> = sut.updateNote("Updated Title", "Updated Content")
        single.subscribe()

        //  force note reload
        val note = sfs.loadFile(noteFile) as Note
        errorCollector.checkThat(note.title, `is`("Updated Title"))
        errorCollector.checkThat(note.content, `is`("Updated Content"))
    }

}