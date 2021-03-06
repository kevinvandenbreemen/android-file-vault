package com.vandenbreemen.secretcamera.mvp.gallery

import android.os.Environment
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.FileMeta
import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.extListFiles
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
class ImageFilesInteractorTest {

    lateinit var credentials: SFSCredentials

    lateinit var imageFilesInteractor: ImageFilesInteractor

    @Before
    fun setup() {
        RxJavaPlugins.setComputationSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }
        ShadowLog.stream = System.out

        val sfsFile = File(Environment.getExternalStorageDirectory().toString() + File.separator + "test")
        val tempPassword = "password"
        val testPassword = SecureFileSystem.generatePassword(SecureString.fromPassword(tempPassword))
        credentials = SFSCredentials(sfsFile, testPassword)

        //  Stand up test image files
        for (i in 1..10) {
            sfs().storeObject("img_$i", "TEST_IMAGE_$i")
            sfs().setFileMetadata("img_$i", FileMeta(PicturesFileTypes.IMPORTED_IMAGE))
        }

        this.imageFilesInteractor = ImageFilesInteractor(sfs())
    }

    private fun sfs(): SecureFileSystem {
        return object : SecureFileSystem(credentials.fileLocation) {
            override fun getPassword(): SecureString = credentials.password.copy()
        }
    }

    @Test
    fun shouldListFilesInAscendingOrder() {
        assertEquals("first image", "img_1", imageFilesInteractor.listImageFiles()[0])
    }

    @Test
    fun shouldIncludeCapturedImages() {
        sfs().storeObject("captured", "CApturedIMG")
        sfs().setFileMetadata("captured", FileMeta(PicturesFileTypes.CAPTURED_IMAGE))
        this.imageFilesInteractor = ImageFilesInteractor(sfs())
        assertTrue("Captured image", imageFilesInteractor.listImageFiles().contains("captured"))
    }

    @Test
    fun shouldCacheImageList() {

        val secureFileSystem = sfs()
        imageFilesInteractor = ImageFilesInteractor(secureFileSystem)
        imageFilesInteractor.listImageFiles()

        //  Destroy SFS files to get at the cache!
        for (i in 1..10) {
            secureFileSystem.deleteFile("img_$i")
        }

        val list = imageFilesInteractor.listImageFiles()

        assertEquals("cached files", 10, list.size)
    }

    @Test(expected = Exception::class)
    fun shouldDestroyCache() {

        val secureFileSystem = sfs()
        imageFilesInteractor = ImageFilesInteractor(secureFileSystem)
        imageFilesInteractor.listImageFiles()

        //  Destroy SFS files to get at the cache!
        for (i in 1..10) {
            secureFileSystem.deleteFile("img_$i")
        }

        imageFilesInteractor.close()

        imageFilesInteractor.listImageFiles()

    }

    @Test
    fun `Should note delete non-image files`() {
        //  Arrange
        sfs().storeObject("NotAnImage", "This is a test")
        sfs().setFileMetadata("NotAnImage", FileMeta(FileTypes.DATA))
        sfs().storeObject("NoType", arrayListOf("Larry", "Curly", "Moe"))

        imageFilesInteractor = ImageFilesInteractor(sfs())

        //  Act
        imageFilesInteractor.deleteImages(imageFilesInteractor.listImageFiles())

        assertEquals(2, sfs().extListFiles().size)
        assertEquals(arrayListOf("Larry", "Curly", "Moe"), sfs().loadFile("NoType"))
    }

    @Test
    fun `should delete all image files`() {
        imageFilesInteractor = ImageFilesInteractor(sfs())

        //  Act
        imageFilesInteractor.deleteImages(imageFilesInteractor.listImageFiles())

        assertEquals(0, sfs().extListFiles().size)
    }

}