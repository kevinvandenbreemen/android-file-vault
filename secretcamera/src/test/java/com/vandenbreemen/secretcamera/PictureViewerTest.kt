package com.vandenbreemen.secretcamera

import android.content.Intent
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.FileMeta
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.mobilesecurestorage.security.crypto.setFileMetadata
import com.vandenbreemen.secretcamera.mvp.gallery.PicturesFileTypes
import com.vandenbreemen.secretcamera.shittySolutionPleaseDelete.TestConstants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
class PictureViewerTest {

    lateinit var sfsCredentials: SFSCredentials

    lateinit var sfs: SecureFileSystem

    lateinit var intent: Intent

    @Before
    fun setup() {
        RxJavaPlugins.setComputationSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }

        val file = createTempFile("note_test")
        sfsCredentials = SFSCredentials(file, SecureFileSystem.generatePassword(SecureString.fromPassword("test")))
        sfs = object : SecureFileSystem(sfsCredentials.fileLocation) {
            override fun getPassword(): SecureString {
                return sfsCredentials.password
            }
        }

        intent = Intent(ShadowApplication.getInstance().applicationContext, PictureViewerActivity::class.java)
        intent.putExtra(SFSCredentials.PARM_CREDENTIALS, sfsCredentials)
    }

    @Test
    fun shouldStartActivity() {
        buildActivity(PictureViewerActivity::class.java, intent)
                .create()
                .resume()
                .get()
    }

    @Test
    fun shouldShowPicture() {

        //  Arrange
        sfs.importFile(TestConstants.TEST_RES_IMG_1)
        sfs.setFileMetadata(TestConstants.TEST_RES_IMG_1.name, FileMeta(PicturesFileTypes.IMPORTED_IMAGE))

        val activity = buildActivity(PictureViewerActivity::class.java, intent)
                .create()
                .resume()
                .get()

        val view = activity.findViewById<SubsamplingScaleImageView>(R.id.currentImage)
        assertTrue("Image rendered", view.hasImage())

    }

    @Test
    fun shouldGracefullyHandleNoAvailableImages() {
        val activity = buildActivity(PictureViewerActivity::class.java, intent)
                .create()
                .resume()
                .get()

        assertEquals("No image Message", "No images available", ShadowToast.getTextOfLatestToast())
    }

}