package com.vandenbreemen.secretcamera.mvp.gallery

import android.os.Environment
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.FileMeta
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.mobilesecurestorage.security.crypto.setFileMetadata
import com.vandenbreemen.secretcamera.shittySolutionPleaseDelete.TestConstants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import junit.framework.TestCase.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
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
class PictureViewerPresenterTest {
    lateinit var credentials: SFSCredentials

    lateinit var model: PictureViewerModel

    lateinit var pictureViewerPresenter: PictureViewerPresenter

    @get:Rule
    val rule = MockitoJUnit.rule()

    @Mock
    lateinit var view: PictureViewerView

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

        pictureViewerPresenter = PictureViewerPresenterImpl(this.model, view)
    }

    private fun sfs(): SecureFileSystem {
        return object : SecureFileSystem(credentials.fileLocation) {
            override fun getPassword(): SecureString = credentials.password.copy()
        }
    }

    @Test
    fun shouldNotSetCurrentImageWhenLoadingThumbnail() {
        pictureViewerPresenter.thumbnail(TestConstants.TEST_RES_IMG_3.name).blockingGet()
        assertFalse("Current image", TestConstants.TEST_RES_IMG_3.name.equals(model.currentFile().blockingGet()))
    }
}