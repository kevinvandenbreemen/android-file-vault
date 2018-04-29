package com.vandenbreemen.secretcamera.mvp.takepicture

import android.os.Environment
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.DateInteractor
import com.vandenbreemen.mobilesecurestorage.security.Bytes
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.getFileMeta
import com.vandenbreemen.mobilesecurestorage.security.crypto.listFiles
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.secretcamera.mvp.gallery.PicturesFileTypes
import com.vandenbreemen.secretcamera.shittySolutionPleaseDelete.TestConstants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import junit.framework.TestCase.assertTrue
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ErrorCollector
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog
import java.io.File
import java.util.*

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
@RunWith(RobolectricTestRunner::class)
class TakePictureModelTest {

    @get:Rule
    val errorCollector: ErrorCollector = ErrorCollector()

    @get:Rule
    val mockitoRule = MockitoJUnit.rule()

    lateinit var takePictureModel: TakePictureModel

    @Mock
    lateinit var dateInteractor: DateInteractor

    lateinit var credentials: SFSCredentials


    @Before
    fun setup() {
        RxJavaPlugins.setComputationSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }
        ShadowLog.stream = System.out

        `when`(dateInteractor.getDateTime()).thenReturn(Calendar.getInstance())

        val sfsFile = File(Environment.getExternalStorageDirectory().toString() + File.separator + "test")
        val tempPassword = "password"
        val testPassword = SecureFileSystem.generatePassword(SecureString.fromPassword(tempPassword))
        credentials = SFSCredentials(sfsFile, testPassword)

        this.takePictureModel = TakePictureModel(dateInteractor, credentials)
        this.takePictureModel.init().subscribe()

    }

    private fun sfs(): SecureFileSystem {
        return object : SecureFileSystem(credentials.fileLocation) {
            override fun getPassword(): SecureString = credentials.password.copy()
        }
    }

    @Test
    fun shouldStorePicture() {
        val bytes = Bytes.loadBytesFromFile(TestConstants.TEST_RES_IMG_2)
        this.takePictureModel.storePicture(bytes).blockingGet()

        errorCollector.checkThat("Store image", sfs().listFiles(PicturesFileTypes.CAPTURED_IMAGE).size, `is`(1))
    }

    @Test
    fun shouldCorrectlyNameImage() {

        `when`(dateInteractor.getDateTime()).thenReturn(Calendar.Builder()
                .set(Calendar.YEAR, 2018)
                .set(Calendar.MONTH, 3)
                .set(Calendar.DAY_OF_MONTH, 29)
                .set(Calendar.HOUR_OF_DAY, 12)
                .set(Calendar.MINUTE, 24)
                .set(Calendar.SECOND, 1)
                .build()
        )

        val expectedFileName = "CAP_20180429122401"
        val bytes = Bytes.loadBytesFromFile(TestConstants.TEST_RES_IMG_2)
        this.takePictureModel.storePicture(bytes).blockingGet()

        errorCollector.checkThat("Image stored", sfs().exists(expectedFileName), `is`(true))
        errorCollector.checkThat("Image type",
                PicturesFileTypes.CAPTURED_IMAGE.equals(sfs().getFileMeta(expectedFileName)!!.getFileType()), `is`(true))
    }

    @Test
    fun shouldMakeImagesCapturedInSameSecondUnique() {

        `when`(dateInteractor.getDateTime()).thenReturn(Calendar.Builder()
                .set(Calendar.YEAR, 2018)
                .set(Calendar.MONTH, 3)
                .set(Calendar.DAY_OF_MONTH, 29)
                .set(Calendar.HOUR_OF_DAY, 12)
                .set(Calendar.MINUTE, 24)
                .set(Calendar.SECOND, 1)
                .build()
        )

        val bytes = Bytes.loadBytesFromFile(TestConstants.TEST_RES_IMG_2)
        this.takePictureModel.storePicture(bytes).blockingGet()
        this.takePictureModel.storePicture(bytes).blockingGet()
        this.takePictureModel.storePicture(bytes).blockingGet()

        errorCollector.checkThat("Multiple images", sfs().listFiles(PicturesFileTypes.CAPTURED_IMAGE).size, `is`(3))
        val fileNames = sfs().listFiles(PicturesFileTypes.CAPTURED_IMAGE)
        assertTrue("First image", fileNames.contains("CAP_20180429122401"))
        assertTrue("Second image", fileNames.contains("CAP_20180429122401_1"))
        assertTrue("Third image", fileNames.contains("CAP_20180429122401_2"))
    }

}