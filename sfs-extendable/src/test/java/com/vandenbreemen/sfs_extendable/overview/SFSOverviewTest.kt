package com.vandenbreemen.sfs_extendable.overview

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.sfs_extendable.app.SFExtentableApp
import com.vandenbreemen.sfs_test_utils.SFSTestingUtils
import junit.framework.TestCase.assertEquals
import kotlinx.android.synthetic.main.activity_s_f_s_overview.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author kevin
 */
@RunWith(AndroidJUnit4::class)
class SFSOverviewTest {

    @Test
    fun `Launch Activity`() {

        //  Arrange
        val file = SFSTestingUtils.getTestFile("testFile")
        val sfs = SFSTestingUtils.getNewSecureFileSystem(file)
        sfs.touch("file1")

        val intent = Intent(ApplicationProvider.getApplicationContext<SFExtentableApp>(), SFSOverview::class.java)
        val credentials = SFSCredentials(file,
                SecureFileSystem.generatePassword(SecureString("password123".toByteArray())))

        intent.putExtra(SFSCredentials.PARM_CREDENTIALS, credentials)

        val scenario = launchActivity<SFSOverview>(intent)
        scenario.moveToState(Lifecycle.State.CREATED)

        scenario.onActivity { activity ->
            assertEquals("1", activity.filesCount.text)
        }

    }

}