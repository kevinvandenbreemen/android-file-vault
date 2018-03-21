package com.vandenbreemen.secretcamera.mvp.impl

import android.os.Environment
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.secretcamera.mvp.impl.TakeNewNoteModel.Companion.storeNote
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
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
class SFSMainMenuModelTest {

    lateinit var credentials: SFSCredentials

    lateinit var sut: SFSMainMenuModel

    @Before
    fun setup() {

        RxJavaPlugins.setIoSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }
        ShadowLog.stream = System.out

        val sfsFile = File(Environment.getExternalStorageDirectory().toString() + File.separator + "test")
        val tempPassword = "password"
        val testPassword = SecureFileSystem.generatePassword(SecureString.fromPassword(tempPassword))
        credentials = SFSCredentials(sfsFile, testPassword)
        sut = SFSMainMenuModel(credentials)

        sut.init().subscribe()
    }

    @Test
    fun shouldListNotes() {

        //  Arrange
        storeNote(sut.getSFS(), "Test Note", "Test not content")

        //  Act
        val singleList: Single<List<String>> = sut.getNotes()

        //  assert
        singleList.test()
                .assertComplete()
                .assertValue { it.size == 1 }

    }

    @Test
    fun shouldListOnlyNotes() {
        //  Arrange
        storeNote(sut.getSFS(), "Test Note", "Test not content")
        sut.getSFS().storeObject("NotANote", "I'm not a note")

        //  Act
        val singleList: Single<List<String>> = sut.getNotes()

        //  assert
        singleList.test()
                .assertComplete()
                .assertValue { it.size == 1 }
    }

}