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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog
import java.io.File

@RunWith(RobolectricTestRunner::class)
class PictureViewerPresenterImplTest {

    lateinit var credentials: SFSCredentials

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    lateinit var sut: PictureViewerPresenter

    @Mock
    lateinit var view: PictureViewerView

    @Mock
    lateinit var router: PictureViewRouter

    @Before
    fun setup() {

        ShadowLog.stream = System.out

        RxJavaPlugins.setComputationSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }

        val sfsFile = File(Environment.getExternalStorageDirectory().toString() + File.separator + "test${System.currentTimeMillis()}")
        val tempPassword = "password"
        val testPassword = SecureFileSystem.generatePassword(SecureString.fromPassword(tempPassword))
        credentials = SFSCredentials(sfsFile, testPassword)

        //  Arrange
        sfs().importFile(TestConstants.NON_IMAGE)
        sfs().setFileMetadata(TestConstants.NON_IMAGE.name, FileMeta(PicturesFileTypes.IMPORTED_IMAGE))
        sfs().importFile(TestConstants.TEST_RES_IMG_1)
        sfs().setFileMetadata(TestConstants.TEST_RES_IMG_1.name, FileMeta(PicturesFileTypes.IMPORTED_IMAGE))

        sut = PictureViewerPresenterImpl(
            PictureViewerModel(credentials),
                view, router
        )
        sut.start()
    }

    @Test
    @Config(shadows = [FailingBitmapFactory::class])
    fun loadBitmapShouldGracefullyHandleFailingImageLoad() {
        val result = sut.fetchThumbnail(TestConstants.NON_IMAGE.name)

        assertNull(result)
    }

    @Test
    fun loadBitmapShouldWork() {
        //
        val result = sut.fetchThumbnail(TestConstants.TEST_RES_IMG_1.name)
        assertNotNull(result)
    }

    private fun sfs(): SecureFileSystem {
        return object : SecureFileSystem(credentials.fileLocation) {
            override fun getPassword(): SecureString = credentials.password.copy()
        }
    }

}