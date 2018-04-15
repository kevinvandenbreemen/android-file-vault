package com.vandenbreemen.secretcamera.mvp.gallery

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.FileMeta
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.mobilesecurestorage.security.crypto.setFileMetadata
import com.vandenbreemen.secretcamera.shittySolutionPleaseDelete.TestConstants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ErrorCollector
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
@RunWith(RobolectricTestRunner::class)
class PictureViewerContractTest {

    lateinit var credentials: SFSCredentials

    lateinit var sfs: SecureFileSystem

    @get:Rule
    val errorCollector: ErrorCollector = ErrorCollector()

    @Before
    fun setup() {
        RxJavaPlugins.setComputationSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }
        ShadowLog.stream = System.out

        val sfsFile = createTempFile("picVContract", "test")
        val tempPassword = "password"
        val testPassword = SecureFileSystem.generatePassword(SecureString.fromPassword(tempPassword))
        credentials = SFSCredentials(sfsFile, testPassword)

        sfs = object : SecureFileSystem(credentials.fileLocation) {
            override fun getPassword(): SecureString = credentials.password
        }
    }

    @Test
    fun shouldBeAbleToFindImages() {

        //  Arrange
        sfs.importFile(TestConstants.TEST_RES_IMG_1)
        sfs.setFileMetadata(TestConstants.TEST_RES_IMG_1.name, FileMeta(PicturesFileTypes.IMPORTED_IMAGE))

        //  Act
        val interactor = ImageFilesInteractor(sfs)
        val filesList: List<String> = interactor.listImageFiles()

        //  Assert
        errorCollector.checkThat("Images", filesList.isEmpty(), `is`(false))
        errorCollector.checkThat("Image file ${TestConstants.TEST_RES_IMG_1.name}",
                filesList.contains(TestConstants.TEST_RES_IMG_1.name), `is`(true))
        errorCollector.checkThat("Single image", filesList.size, `is`(1))
    }

}