package com.vandenbreemen.secretcamera.mvp.notes

import android.os.Environment
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.extListFiles
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.secretcamera.api.Note
import com.vandenbreemen.secretcamera.mvp.impl.TakeNewNoteModel
import com.vandenbreemen.secretcamera.mvp.impl.TakeNewNotePresenterImpl
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class TakeNewNotePresenterTest {

    @get:Rule
    val rule = MockitoJUnit.rule()

    lateinit var takeNewNotePresenter: TakeNewNotePresenter

    @Mock
    lateinit var takeNoteView: TakeNewNoteView

    lateinit var sfsFile: File

    var closed = false

    @Before
    fun setup() {
        closed = false

        RxJavaPlugins.setIoSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }
        RxJavaPlugins.setComputationSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }
        sfsFile = File(Environment.getExternalStorageDirectory().toString() + File.separator + "sfs")

        takeNewNotePresenter = TakeNewNotePresenterImpl(takeNoteView,
                object : TakeNewNoteModel(SFSCredentials(sfsFile, createPassword())) {
                    override fun onClose() {
                        closed = true
                    }
                }
        )
        takeNewNotePresenter.start()
    }

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

    @Test
    fun shouldOnlySaveOnce() {
        takeNewNotePresenter.provideNoteDetails("new note", "note content")
        takeNewNotePresenter.saveAndClose("new note", "note content").subscribe()
        assertEquals("Single file", 1, sfs().extListFiles().size)
        val note = sfs().loadFile(sfs().extListFiles()[0]) as Note
        assertEquals("Title", "new note", note.title)
        assertEquals("Content", "note content", note.content)
    }

    @Test
    fun shouldSaveAndClose() {
        takeNewNotePresenter.saveAndClose("new note", "note content").subscribe()
        assertEquals("Single file", 1, sfs().extListFiles().size)
        val note = sfs().loadFile(sfs().extListFiles()[0]) as Note
        assertEquals("Title", "new note", note.title)
        assertEquals("Content", "note content", note.content)

    }

    @Test
    fun shouldCloseModelDuringSaveAndClose() {
        takeNewNotePresenter.saveAndClose("new note", "note content").subscribe()
        assertTrue("Closed", closed)
    }

}