package com.vandenbreemen.secretcamera.mvp.gallery

import android.graphics.Bitmap
import android.os.Environment
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.FileMeta
import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.getFileMeta
import com.vandenbreemen.mobilesecurestorage.security.crypto.listFiles
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.mobilesecurestorage.security.crypto.setFileMetadata
import com.vandenbreemen.secretcamera.shittySolutionPleaseDelete.TestConstants
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import junit.framework.TestCase.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadows.ShadowLog
import java.io.File

var generateThumbnailCalled = false
var generateThumbnailSynchronousCalled = false

@Implements(AndroidImageInteractor::class)
class ShadowAndroidImageInteractor {

    @Implementation
    fun generateThumbnail(bitmap: Bitmap): Single<Bitmap> {
        generateThumbnailCalled = true
        return Single.just(mock(Bitmap::class.java))
    }

    @Implementation
    fun generateThumbnailSynchronous(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        generateThumbnailSynchronousCalled = true
        return mock(Bitmap::class.java)
    }

    @Implementation
    fun convertByteArrayToBitmapSynchronous(imageBytes: ByteArray): Bitmap {
        return mock(Bitmap::class.java)
    }
}

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
@RunWith(RobolectricTestRunner::class)
class PictureViewerModelNavigationTest {

    lateinit var credentials: SFSCredentials

    lateinit var model: PictureViewerModel

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var imageBitmap: Bitmap

    @Before
    fun setup() {
        generateThumbnailCalled = false
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

    @Test
    fun shouldProvideImageList() {
        val imageList = model.listImages().blockingGet()
        for (i in 1..10) {
            assertTrue("Image img_$i in list", imageList.contains("img_$i"))
        }
    }

    @Test
    @Config(shadows = [ShadowAndroidImageInteractor::class])
    fun shouldGetThumbnail() {
        model.getThumbnail(imageBitmap).subscribe()
        assertTrue("Generate thumbnail", generateThumbnailCalled)
    }

}

//  Test created to side-step the naming conventions used in the step-through test above
@RunWith(RobolectricTestRunner::class)
class PictureViewerModelTest {

    lateinit var credentials: SFSCredentials

    lateinit var model: PictureViewerModel

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var imageBitmap: Bitmap

    @Before
    fun setup() {
        generateThumbnailCalled = false
        RxJavaPlugins.setComputationSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }
        ShadowLog.stream = System.out

        val sfsFile = File(Environment.getExternalStorageDirectory().toString() + File.separator + "test${System.currentTimeMillis()}")
        val tempPassword = "password"
        val testPassword = SecureFileSystem.generatePassword(SecureString.fromPassword(tempPassword))
        credentials = SFSCredentials(sfsFile, testPassword)

        //  Stand up test image files
        sfs().importFile(TestConstants.TEST_RES_IMG_1)
        sfs().setFileMetadata(TestConstants.TEST_RES_IMG_1.name, FileMeta(PicturesFileTypes.IMPORTED_IMAGE))
        sfs().importFile(TestConstants.TEST_RES_IMG_2)
        sfs().setFileMetadata(TestConstants.TEST_RES_IMG_2.name, FileMeta(PicturesFileTypes.IMPORTED_IMAGE))
        sfs().importFile(TestConstants.TEST_RES_IMG_3)
        sfs().setFileMetadata(TestConstants.TEST_RES_IMG_3.name, FileMeta(PicturesFileTypes.IMPORTED_IMAGE))

        this.model = PictureViewerModel(credentials)
        this.model.init().subscribe()
    }

    private fun sfs(): SecureFileSystem {
        return object : SecureFileSystem(credentials.fileLocation) {
            override fun getPassword(): SecureString = credentials.password.copy()
        }
    }

    @Test
    fun shouldUpdateCurrentImageOnSelectImage() {
        model.loadImageForDisplay(TestConstants.TEST_RES_IMG_2.name).blockingGet()
        assertEquals("Current image", TestConstants.TEST_RES_IMG_2.name, model.currentFile().blockingGet())
    }

    @Test
    @Config(shadows = [ShadowImageFilesInteractor::class])
    fun shouldCloseInteractor() {
        model.onClose()
        assertTrue("Closed", imageFilesInteractorClosed)
    }

    @Test
    @Config(shadows = [FailingBitmapFactory::class])
    fun shouldGracefullyHandleFailedImageLoad() {
        //  Arrange
        sfs().importFile(TestConstants.NON_IMAGE)
        sfs().setFileMetadata(TestConstants.NON_IMAGE.name, FileMeta(PicturesFileTypes.IMPORTED_IMAGE))
        this.model = PictureViewerModel(credentials)
        this.model.init().subscribe()

        //  Act
        val observer = model.loadImage(TestConstants.NON_IMAGE.name).test()
        assertEquals(1, observer.errorCount())
    }

    @Test
    fun shouldDeleteAllImages() {
        //  Act
        model.deleteAllImages().test().assertComplete()

        //  Assert
        assertTrue(sfs().listFiles(PicturesFileTypes.IMPORTED_IMAGE).isEmpty())
    }

    @Test
    fun shouldNotPreserveCurrentFileAfterDeleteAllImages() {
        //  Arrange
        model.loadImageForDisplay(TestConstants.TEST_RES_IMG_1.name)
        println(model.currentFile().blockingGet())

        //  Act
        model.deleteAllImages().test().assertComplete()

        //  Assert
        var validated = false
        model.currentFile().subscribe({ img ->
            fail("Should not have succeeded")
        }, { err ->
            System.err.println(err.localizedMessage)
            validated = true
        })

        assertTrue("System should have failed to get the current file since it was deleted", validated)
    }

}