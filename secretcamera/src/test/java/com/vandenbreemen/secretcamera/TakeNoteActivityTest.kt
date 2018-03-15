package com.vandenbreemen.secretcamera

import android.content.Intent
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import junit.framework.TestCase.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowLog
import java.io.File

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
@RunWith(RobolectricTestRunner::class)
class TakeNoteActivityTest {

    private var directory: File? = null

    lateinit var testPassword: SecureString

    lateinit var intent: Intent

    lateinit var sfsFile: File

    @Before
    fun setup() {

        RxJavaPlugins.setIoSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }
        ShadowLog.stream = System.out

        sfsFile = File(Environment.getExternalStorageDirectory().toString() + File.separator + "test")
        val tempPassword = "password"
        testPassword = SecureFileSystem.generatePassword(SecureString.fromPassword(tempPassword))

        //  Stand up SFS


        intent = Intent(ShadowApplication.getInstance().applicationContext, TakeNoteActivity::class.java)
        intent.putExtra(SFSCredentials.PARM_CREDENTIALS, SFSCredentials(sfsFile, testPassword))
    }

    @Test
    fun sanityTestStart() {
        buildActivity(TakeNoteActivity::class.java, intent)
                .create()
                .resume()
                .get()
    }

    @Test
    fun shouldSaveNewFileWhenSavingANote() {
        val activity = buildActivity(TakeNoteActivity::class.java, intent)
                .create()
                .resume()
                .get()

        activity.findViewById<TextView>(R.id.title).setText("Test Note")
        activity.findViewById<TextView>(R.id.content).setText("Testing creating a new new\nnote.  This is ultra secret\ninformation blablabla")
        activity.findViewById<Button>(R.id.ok).performClick()

        val secureFileSystem = object : SecureFileSystem(sfsFile) {
            override fun getPassword(): SecureString {
                return testPassword
            }
        }

        assertFalse("new note stored", secureFileSystem.listFiles().isEmpty())
    }

    @Test
    fun shouldNotSaveNewNoteWhenCancelling() {
        val activity = buildActivity(TakeNoteActivity::class.java, intent)
                .create()
                .resume()
                .get()

        activity.findViewById<TextView>(R.id.title).setText("Test Note")
        activity.findViewById<TextView>(R.id.content).setText("Testing creating a new new\nnote.  This is ultra secret\ninformation blablabla")
        activity.findViewById<Button>(R.id.cancel).performClick()

        val secureFileSystem = object : SecureFileSystem(sfsFile) {
            override fun getPassword(): SecureString {
                return testPassword
            }
        }

        assertTrue("new note stored", secureFileSystem.listFiles().isEmpty())
    }

    @Test
    fun shouldReturnToMainScreenOnCancel() {
        val activity = buildActivity(TakeNoteActivity::class.java, intent)
                .create()
                .resume()
                .get()

        activity.findViewById<TextView>(R.id.title).setText("Test Note")
        activity.findViewById<TextView>(R.id.content).setText("Testing creating a new new\nnote.  This is ultra secret\ninformation blablabla")
        activity.findViewById<Button>(R.id.cancel).performClick()

        val nextActivityIntent = shadowOf(activity).nextStartedActivity
        val intent = shadowOf(nextActivityIntent)
        assertEquals("Go to main", MainActivity::class.java, intent.intentClass)
        assertNotNull("Credentials", nextActivityIntent.getParcelableExtra(SFSCredentials.PARM_CREDENTIALS))
    }

    @Test
    fun shouldReturnToMainScreenOnSuccess() {
        val activity = buildActivity(TakeNoteActivity::class.java, intent)
                .create()
                .resume()
                .get()

        activity.findViewById<TextView>(R.id.title).setText("Test Note")
        activity.findViewById<TextView>(R.id.content).setText("Testing creating a new new\nnote.  This is ultra secret\ninformation blablabla")
        activity.findViewById<Button>(R.id.ok).performClick()

        val nextActivityIntent = shadowOf(activity).nextStartedActivity
        val intent = shadowOf(nextActivityIntent)
        assertEquals("Go to main", MainActivity::class.java, intent.intentClass)
        assertNotNull("Credentials", nextActivityIntent.getParcelableExtra(SFSCredentials.PARM_CREDENTIALS))
    }

}