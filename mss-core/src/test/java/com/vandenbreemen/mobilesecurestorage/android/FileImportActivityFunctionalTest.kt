package com.vandenbreemen.mobilesecurestorage.android

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Environment
import com.vandenbreemen.mobilesecurestorage.android.api.FileWorkflow
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.ShadowModel
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.closed
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.extListFiles
import com.vandenbreemen.mobilesecurestorage.security.crypto.getFileMeta
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.io.File

/**
 * Created by kevin on 02/04/18.
 */
@RunWith(RobolectricTestRunner::class)
class FileImportActivityFunctionalTest {

    lateinit var directoryToImport: File

    lateinit var sfsFile: File

    @Before
    fun setup() {
        RxJavaPlugins.setIoSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }
        RxJavaPlugins.setComputationSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }
        directoryToImport = File(Environment.getExternalStorageDirectory().toString() + File.separator + "toImport")
        directoryToImport.mkdir()
        sfsFile = File(Environment.getExternalStorageDirectory().toString() + File.separator + "sfs")
    }

    fun createPassword(): SecureString {
        return SecureFileSystem.generatePassword(SecureString.fromPassword("password"))
    }

    fun sfs(): SecureFileSystem {
        return object : SecureFileSystem(sfsFile) {
            override fun getPassword(): SecureString {
                return createPassword()
            }

        }
    }

    @Test
    fun shouldImportFilesInDirectoryIntoSFS() {
        //  Arrange
        var file = File(directoryToImport.absolutePath + File.separator + "file1")
        file.createNewFile()
        file = File(directoryToImport.absolutePath + File.separator + "file2")
        file.createNewFile()
        file = File(directoryToImport.absolutePath + File.separator + "file3")
        file.createNewFile()

        val workflow = FileWorkflow()
        workflow.fileOrDirectory = directoryToImport

        val intent = Intent(RuntimeEnvironment.application, FileImportActivity::class.java)
        intent.putExtra(FileWorkflow.PARM_WORKFLOW_NAME, workflow)
        intent.putExtra(SFSCredentials.PARM_CREDENTIALS,
                SFSCredentials(sfsFile, createPassword()))

        val sut: FileImportActivity = buildActivity(FileImportActivity::class.java, intent)
                .create()
                .resume()
                .get()

        assertEquals("File import count", 3, sfs().listFiles().size)

    }

    @Test
    @Config(shadows = [ShadowModel::class])
    fun shouldCloseOnDone() {
        //  Arrange
        var file = File(directoryToImport.absolutePath + File.separator + "file1")
        file.createNewFile()
        file = File(directoryToImport.absolutePath + File.separator + "file2")
        file.createNewFile()
        file = File(directoryToImport.absolutePath + File.separator + "file3")
        file.createNewFile()

        val workflow = FileWorkflow()
        workflow.fileOrDirectory = directoryToImport

        val intent = Intent(RuntimeEnvironment.application, FileImportActivity::class.java)
        intent.putExtra(FileWorkflow.PARM_WORKFLOW_NAME, workflow)
        intent.putExtra(SFSCredentials.PARM_CREDENTIALS,
                SFSCredentials(sfsFile, createPassword()))

        val activityController = buildActivity(FileImportActivity::class.java, intent)
                .create()
                .resume()
        activityController.get()

        assertTrue("Closed", closed)

    }

    @Test
    fun shouldAddFileTypeToImportedFiles() {
//  Arrange
        var file = File(directoryToImport.absolutePath + File.separator + "file1")
        file.createNewFile()
        file = File(directoryToImport.absolutePath + File.separator + "file2")
        file.createNewFile()
        file = File(directoryToImport.absolutePath + File.separator + "file3")
        file.createNewFile()

        val workflow = FileWorkflow()
        workflow.fileOrDirectory = directoryToImport

        val intent = Intent(RuntimeEnvironment.application, FileImportActivity::class.java)
        intent.putExtra(FileImportActivity.PARM_FILE_TYPE_BYTES, byteArrayOf(FileTypes.DATA.firstByte, FileTypes.DATA.secondByte!!))
        intent.putExtra(SFSCredentials.PARM_CREDENTIALS,
                SFSCredentials(sfsFile, createPassword()))

        val sut: FileImportActivity = buildActivity(FileImportActivity::class.java, intent)
                .create()
                .resume()
                .get()

        val startedActivityForResult = shadowOf(sut).nextStartedActivityForResult
        val resultIntent = Intent()
        resultIntent.putExtra(FileWorkflow.PARM_WORKFLOW_NAME, workflow)
        shadowOf(sut).receiveResult(startedActivityForResult.intent, RESULT_OK, resultIntent)

        assertEquals("File import count", 3, sfs().extListFiles().size)
        sfs().extListFiles().forEach { file ->
            run {
                assertEquals("File type", FileTypes.DATA, sfs().getFileMeta(file)?.getFileType())
            }
        }
    }

}