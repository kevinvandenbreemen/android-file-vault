package com.vandenbreemen.secretcamera.mvp.impl

import android.os.Environment
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.extListFiles
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog
import java.io.File

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
@RunWith(RobolectricTestRunner::class)
class TakeNewNoteModelTest {

    lateinit var sut: TakeNewNoteModel

    lateinit var credentials: SFSCredentials

    @Before
    fun setup() {

        RxJavaPlugins.setComputationSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }
        ShadowLog.stream = System.out

        val sfsFile = File(Environment.getExternalStorageDirectory().toString() + File.separator + "test")
        val tempPassword = "password"
        val testPassword = SecureFileSystem.generatePassword(SecureString.fromPassword(tempPassword))
        credentials = SFSCredentials(sfsFile, testPassword)
        sut = TakeNewNoteModel(credentials)

        sut.init().subscribe()
    }

    @Test(expected = ApplicationError::class)
    fun testMissingTitle() {
        sut.submitNewNote("", "Test note content")
    }

    @Test(expected = ApplicationError::class)
    fun testMissingNoteContent() {
        sut.submitNewNote("Test Title", "")
    }

    @Test
    fun testSavesNote() {
        sut.submitNewNote("Test Title", "Content of the Note").subscribe()
        //  Stand up SFS
        val secureFileSystem = object : SecureFileSystem(credentials.fileLocation) {
            override fun getPassword(): SecureString {
                return credentials.password
            }
        }
        assertEquals("File stored", 1, secureFileSystem.extListFiles().size)
    }

    @Test
    fun shouldSubmitNewNoteOnlyOnce() {
        sut.submitNewNote("Test Title", "Content of the Note").subscribe()
        sut.submitNewNote("Test Title", "Content of the Note").subscribe()
        val secureFileSystem = object : SecureFileSystem(credentials.fileLocation) {
            override fun getPassword(): SecureString {
                return credentials.password
            }
        }
        assertEquals("File stored once", 1, secureFileSystem.extListFiles().size)
    }

    @Test
    fun shouldWipeCredentialsOnDone() {
        sut.submitNewNote("Test Title", "Content of the Note").subscribe()
        sut.close()

        assertTrue("Finalized", credentials.password.isFinalized)
    }

}