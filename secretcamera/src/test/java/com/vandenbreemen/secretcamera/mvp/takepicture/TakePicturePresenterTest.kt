package com.vandenbreemen.secretcamera.mvp.takepicture

import android.os.Environment
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.getDateInteractor
import com.vandenbreemen.mobilesecurestorage.security.Bytes
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.listFiles
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.secretcamera.mvp.gallery.PicturesFileTypes
import com.vandenbreemen.secretcamera.mvp.gallery.generateThumbnailCalled
import com.vandenbreemen.secretcamera.shittySolutionPleaseDelete.TestConstants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ErrorCollector
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadows.ShadowLog
import java.io.File

val fakeCredentials = SFSCredentials(File("no/such"), SecureString.fromPassword("test"))

@Implements(Model::class)
class ShadowTakePictureModel {

    @Implementation
    fun copyCredentials(): SFSCredentials {
        return fakeCredentials
    }

}

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
@RunWith(RobolectricTestRunner::class)
class TakePicturePresenterTest {

    lateinit var credentials: SFSCredentials

    lateinit var takePicturePresenter: TakePicturePresenter

    @get:Rule
    val mockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var view: TakePictureView

    @get:Rule
    val errorCollector = ErrorCollector()

    @Before
    fun setup() {
        generateThumbnailCalled = false
        RxJavaPlugins.setComputationSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }
        ShadowLog.stream = System.out

        val sfsFile = File(Environment.getExternalStorageDirectory().toString() + File.separator + "test${System.currentTimeMillis()}")
        val tempPassword = "password"
        val testPassword = SecureFileSystem.generatePassword(SecureString.fromPassword(tempPassword))
        credentials = SFSCredentials(sfsFile, testPassword)

        val model = TakePictureModel(getDateInteractor(), credentials)
        takePicturePresenter = TakePicturePresenterImpl(model, view)
        takePicturePresenter.start()


    }

    private fun sfs(): SecureFileSystem {
        return object : SecureFileSystem(credentials.fileLocation) {
            override fun getPassword(): SecureString = credentials.password.copy()
        }
    }

    @Test
    fun shouldStoreCapturedImage() {
        val pictureBytes = Bytes.loadBytesFromFile(TestConstants.TEST_RES_IMG_1)
        takePicturePresenter.capture(pictureBytes)

        errorCollector.checkThat("Stored Files", sfs().listFiles(PicturesFileTypes.CAPTURED_IMAGE).size, `is`(1))
    }

    @Test
    @Config(shadows = [ShadowTakePictureModel::class])
    fun shouldReturnUserToMain() {
        takePicturePresenter.back()
        verify(view).returnToMain(fakeCredentials)
    }

}