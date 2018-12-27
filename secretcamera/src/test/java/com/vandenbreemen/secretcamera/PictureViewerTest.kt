package com.vandenbreemen.secretcamera

import android.content.Intent
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
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
import junit.framework.TestCase.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
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
    fun shouldNotShowPictureSelectorOnInitialLoad() {

        //  Arrange
        sfs.importFile(TestConstants.TEST_RES_IMG_1)
        sfs.setFileMetadata(TestConstants.TEST_RES_IMG_1.name, FileMeta(PicturesFileTypes.IMPORTED_IMAGE))

        val activity = buildActivity(PictureViewerActivity::class.java, intent)
                .create()
                .resume()
                .get()

        //  Assert
        assertEquals(GONE, activity.findViewById<RecyclerView>(R.id.pictureSelector).visibility)
    }

    @Test
    fun shouldShowPictureSelectorWhenSelectImageClicked() {
        //  Arrange
        sfs.importFile(TestConstants.TEST_RES_IMG_1)
        sfs.setFileMetadata(TestConstants.TEST_RES_IMG_1.name, FileMeta(PicturesFileTypes.IMPORTED_IMAGE))
        sfs.importFile(TestConstants.TEST_RES_IMG_2)
        sfs.setFileMetadata(TestConstants.TEST_RES_IMG_2.name, FileMeta(PicturesFileTypes.IMPORTED_IMAGE))

        val activity = buildActivity(PictureViewerActivity::class.java, intent)
                .create()
                .resume()
                .get()

        //  Act
        activity.findViewById<FloatingActionButton>(R.id.showSelector).performClick()

        //  Assert
        assertEquals(VISIBLE, activity.findViewById<RecyclerView>(R.id.pictureSelector).visibility)
        val recyclerView = activity.findViewById<RecyclerView>(R.id.pictureSelector)
        recyclerView.measure(0, 0)
        recyclerView.layout(0, 0, 100, 1000)
        assertEquals("Image select items", 2, recyclerView.adapter.itemCount)
    }

    //  TODO    This test doesn't work as the viewHolderForAdaptorAtPosition is always null...
    //  See investigations at https://github.com/robolectric/robolectric/issues/3747
    //@Test
    fun shouldLoadImageWhenSelectorClicked() {
        //  Arrange
        sfs.importFile(TestConstants.TEST_RES_IMG_1)
        sfs.setFileMetadata(TestConstants.TEST_RES_IMG_1.name, FileMeta(PicturesFileTypes.IMPORTED_IMAGE))
        sfs.importFile(TestConstants.TEST_RES_IMG_2)
        sfs.setFileMetadata(TestConstants.TEST_RES_IMG_2.name, FileMeta(PicturesFileTypes.IMPORTED_IMAGE))

        val activity = buildActivity(PictureViewerActivity::class.java, intent)
                .create()
                .resume()
                .get()

        //  Act
        activity.findViewById<FloatingActionButton>(R.id.showSelector).performClick()

        //  Assert
        val recyclerView = activity.findViewById<RecyclerView>(R.id.pictureSelector)
        recyclerView.measure(0, 0)
        recyclerView.layout(0, 0, 100, 1000)
        recyclerView.findViewHolderForAdapterPosition(1).itemView.performClick()
    }

    @Test
    fun shouldGracefullyHandleNoAvailableImages() {
        val activity = buildActivity(PictureViewerActivity::class.java, intent)
                .create()
                .resume()
                .get()

        assertEquals("No image Message", "No images available", ShadowToast.getTextOfLatestToast())
    }

    @Test
    fun shouldNowShowPictureViewerActionsByDefault() {
        val activity = buildActivity(PictureViewerActivity::class.java, intent)
                .create()
                .resume()
                .get()

        //  Assert
        assertEquals(GONE, activity.findViewById<ViewGroup>(R.id.actionsWindow).visibility)
    }

    @Test
    fun shouldHidePictureViewerActionsOnTapOfActionsFAB() {
        //  Arrange
        val activity = buildActivity(PictureViewerActivity::class.java, intent)
                .create()
                .resume()
                .get()


        activity.findViewById<FloatingActionButton>(R.id.actions).performClick()

        //  Act (actions is visible now tap the FAB again)
        activity.findViewById<FloatingActionButton>(R.id.actions).performClick()


        //  Assert
        assertEquals(GONE, activity.findViewById<ViewGroup>(R.id.actionsWindow).visibility)
    }

    @Test
    fun shouldProvideForRemovingAllImageFiles() {
        //  Arrange
        sfs.importFile(TestConstants.TEST_RES_IMG_1)
        sfs.setFileMetadata(TestConstants.TEST_RES_IMG_1.name, FileMeta(PicturesFileTypes.IMPORTED_IMAGE))
        sfs.storeObject("NotAnImage", "This isn't an image")


        val activity = buildActivity(PictureViewerActivity::class.java, intent)
                .create()
                .resume()
                .get()

        //  Act
        activity.findViewById<FloatingActionButton>(R.id.actions).performClick()
        assertEquals("Actions should be visible", VISIBLE, activity.findViewById<ViewGroup>(R.id.actionsWindow).visibility)
        activity.findViewById<ViewGroup>(R.id.actionsWindow).findViewById<Button>(R.id.deleteAllImages).performClick()

        //  Assert
        sfs = object : SecureFileSystem(sfsCredentials.fileLocation) {
            override fun getPassword(): SecureString {
                return sfsCredentials.password
            }
        }
        assertFalse(sfs.exists(TestConstants.TEST_RES_IMG_1.name))
        assertTrue(sfs.exists("NotAnImage"))

        val nextActivity = Shadows.shadowOf(activity).nextStartedActivity
        assertNotNull(nextActivity)

        assertEquals(MainActivity::class.java, Shadows.shadowOf(nextActivity).intentClass)
        assertNotNull(nextActivity.getParcelableExtra(SFSCredentials.PARM_CREDENTIALS))

    }

}