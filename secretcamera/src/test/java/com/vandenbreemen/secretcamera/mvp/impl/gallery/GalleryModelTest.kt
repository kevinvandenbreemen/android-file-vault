package com.vandenbreemen.secretcamera.mvp.impl.gallery

import android.graphics.Bitmap
import android.os.Environment
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.secretcamera.mvp.gallery.PicturesFileTypes
import com.vandenbreemen.secretcamera.mvp.gallery.ShadowAndroidImageInteractor
import com.vandenbreemen.secretcamera.mvp.gallery.generateThumbnailSynchronousCalled
import com.vandenbreemen.secretcamera.shittySolutionPleaseDelete.TestConstants
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * @author kevin
 */
@RunWith(RobolectricTestRunner::class)
class GalleryModelTest {

    lateinit var credentials: SFSCredentials

    lateinit var sfs: SecureFileSystem

    lateinit var model: GalleryModel

    @Before
    fun setup() {

        generateThumbnailSynchronousCalled = false

        RxJavaPlugins.setComputationSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }

        val sfsFile = File(Environment.getExternalStorageDirectory().toString() + File.separator + "test")
        val tempPassword = "password"
        val testPassword = SecureFileSystem.generatePassword(SecureString.fromPassword(tempPassword))
        credentials = SFSCredentials(sfsFile, testPassword)

        sfs = object : SecureFileSystem(credentials.fileLocation) {
            override fun getPassword(): SecureString = credentials.password
        }


    }

    @Test
    @Config(shadows = [ShadowAndroidImageInteractor::class])
    fun `should load up preview thumbnail collection`() {

        //  Arrange
        sfs.importFile(TestConstants.TEST_RES_IMG_1)
        sfs.setFileType(TestConstants.TEST_RES_IMG_1.name, PicturesFileTypes.IMPORTED_IMAGE)
        sfs.importFile(TestConstants.TEST_RES_IMG_2)
        sfs.setFileType(TestConstants.TEST_RES_IMG_2.name, PicturesFileTypes.IMPORTED_IMAGE)
        sfs.importFile(TestConstants.TEST_RES_IMG_3)
        sfs.setFileType(TestConstants.TEST_RES_IMG_3.name, PicturesFileTypes.IMPORTED_IMAGE)
        model = GalleryModel(credentials)
        model.init().blockingGet()

        //  Act
        val thumbnails: Single<List<Bitmap>> = model.getImageThumbnails()

        //  Assert
        val test = thumbnails.test()
        test.assertComplete()

        assertTrue(generateThumbnailSynchronousCalled)


    }

}