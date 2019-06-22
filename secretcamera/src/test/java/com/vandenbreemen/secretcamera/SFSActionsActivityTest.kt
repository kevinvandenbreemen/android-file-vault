package com.vandenbreemen.secretcamera

import android.content.Intent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.test.core.app.ApplicationProvider
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowToast

/**
 * @author kevin
 */
@RunWith(RobolectricTestRunner::class)
class SFSActionsActivityTest {

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

        intent = Intent(ApplicationProvider.getApplicationContext(), SFSActionsActivity::class.java)
        intent.putExtra(SFSCredentials.PARM_CREDENTIALS, sfsCredentials)
    }

    @Test
    fun shouldProvideProgressBarFunctionality() {
        //  Arrange
        val activity = buildActivity(SFSActionsActivity::class.java, intent).create().resume().get()

        //  Act
        activity.setProgressMax(100)

        //  Assert
        assertEquals(VISIBLE, activity.findViewById<ViewGroup>(R.id.progressContainer).visibility)
        val progressBar = activity.findViewById<ViewGroup>(R.id.progressContainer).findViewById<ProgressBar>(R.id.progressBar)
        assertEquals(100, progressBar.max)
    }

    @Test
    fun shouldProvideForUpdatingProgress() {
        //  Arrange
        val activity = buildActivity(SFSActionsActivity::class.java, intent).create().resume().get()
        activity.setProgressMax(100)

        //  Act
        activity.setCurrentProgress(45)

        //  Assert
        val progressBar = activity.findViewById<ViewGroup>(R.id.progressContainer).findViewById<ProgressBar>(R.id.progressBar)
        assertEquals(45, progressBar.progress)
    }

    @Test
    fun shouldHideChangePasswordDialogOnCancel() {
        //  Arrange
        val activity = buildActivity(SFSActionsActivity::class.java, intent).create().resume().get()
        activity.findViewById<Button>(R.id.changePassword).performClick()

        //  Act
        activity.findViewById<Button>(R.id.cancel).performClick()

        //  Assert
        assertEquals(GONE, activity.findViewById<View>(R.id.incl_chane_pass_details).visibility)

    }

    @Test
    fun shouldShowErrorDuringFailedChangePassword() {

        //  Arrange
        val activity = buildActivity(SFSActionsActivity::class.java, intent).create().resume().get()
        activity.findViewById<Button>(R.id.changePassword).performClick()

        //  Act
        activity.findViewById<Button>(R.id.ok).performClick()

        //  Assert
        assertTrue(ShadowToast.shownToastCount() > 0)

    }

}