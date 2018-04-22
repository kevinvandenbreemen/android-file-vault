package com.vandenbreemen.secretcamera.mvp.gallery

import android.os.Environment
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.FileMeta
import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.getFileMeta
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.mobilesecurestorage.security.crypto.setFileMetadata
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
class PictureViewerModelTest {

    lateinit var credentials: SFSCredentials

    lateinit var model: PictureViewerModel

    @Before
    fun setup() {
        RxJavaPlugins.setComputationSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }
        ShadowLog.stream = System.out

        val sfsFile = File(Environment.getExternalStorageDirectory().toString() + File.separator + "test${System.currentTimeMillis()}")
        val tempPassword = "password"
        val testPassword = SecureFileSystem.generatePassword(SecureString.fromPassword(tempPassword))
        credentials = SFSCredentials(sfsFile, testPassword)

        //  Stand up test image files
        for (i in 1..10) {
            sfs().storeObject("img_$i", "TEST_IMAGE_$i")
            sfs().setFileMetadata("img_$i", FileMeta(PicturesFileTypes.IMPORTED_IMAGE))
        }

        this.model = PictureViewerModel(credentials)
        this.model.init().subscribe()
    }

    private fun sfs(): SecureFileSystem {
        return object : SecureFileSystem(credentials.fileLocation) {
            override fun getPassword(): SecureString = credentials.password.copy()
        }
    }

    @Test
    fun shouldDetermineCurrentFile() {
        assertEquals("Current file", "img_1", model.currentFile().blockingGet())
    }

    @Test
    fun shouldLoadCurrentFileFromConfigFile() {
        assertEquals("Current file", "img_1", model.currentFile().blockingGet())
        assertEquals("Current file", "img_1", model.currentFile().blockingGet())

        assertTrue("Config file", sfs().exists(PictureViewerModel.SETTINGS))
        assertEquals("File type", FileTypes.DATA, sfs().getFileMeta(PictureViewerModel.SETTINGS)!!.getFileType())

    }

    @Test
    fun shouldStepToNextFile() {
        model.currentFile().blockingGet()
        assertEquals("Next file", "img_10", model.nextFile().blockingGet())
    }

    @Test
    fun shouldStepToPreviousFile() {
        model.currentFile().blockingGet()
        model.nextFile().blockingGet()
        assertEquals("Prev file", "img_1", model.prevFile().blockingGet())
    }

    @Test
    fun shouldPersistPositionAfterNavigateForward() {
        model.currentFile().blockingGet()
        model.nextFile().blockingGet()
        assertEquals("Current file", "img_10", model.currentFile().blockingGet())
    }

    @Test
    fun shouldPersistPositionAfterNavigateBack() {
        model.currentFile().blockingGet()
        model.nextFile().blockingGet()
        model.nextFile().blockingGet()
        model.nextFile().blockingGet()
        model.prevFile().blockingGet()
        assertEquals("Current file", "img_2", model.currentFile().blockingGet())
    }

    @Test
    fun shouldWrapWhenNavigatingToEnd() {
        model.currentFile().blockingGet()
        for (i in 1..10) {
            model.nextFile().blockingGet()
        }
        assertEquals("Current file", "img_1", model.currentFile().blockingGet())
    }

    @Test
    fun shouldWrapWhenNavigatingBackFromStart() {
        model.currentFile().blockingGet()
        assertEquals("Current file", "img_9", model.prevFile().blockingGet())
    }

}