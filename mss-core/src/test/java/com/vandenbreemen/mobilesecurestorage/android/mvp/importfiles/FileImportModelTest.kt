package com.vandenbreemen.mobilesecurestorage.android.mvp.importfiles

import android.os.Environment
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.extListFiles
import com.vandenbreemen.mobilesecurestorage.security.crypto.getFileMeta
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

/**
 * Created by kevin on 04/04/18.
 */
@RunWith(RobolectricTestRunner::class)
class FileImportModelTest {

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
    fun shouldImportFiles() {

        //  Arrange
        val sfsCredentials = SFSCredentials(sfsFile, createPassword())
        File(directoryToImport.absolutePath + File.separator + "newfile").createNewFile()
        File(directoryToImport.absolutePath + File.separator + "newfile1").createNewFile()

        //  Act
        val fileImportModel = FileImportModel(sfsCredentials)
        fileImportModel.init().subscribe()
        fileImportModel.importDir(directoryToImport, null).test()
                .assertComplete()
                .assertNoErrors()

        //  assert
        assertEquals("Single file imported", 2, sfs().listFiles().size)

    }

    @Test
    fun shouldSetFileTypeWhenImportingFiles() {
        //  Arrange
        val sfsCredentials = SFSCredentials(sfsFile, createPassword())
        File(directoryToImport.absolutePath + File.separator + "newfile").createNewFile()
        File(directoryToImport.absolutePath + File.separator + "newfile1").createNewFile()

        //  Act
        val fileImportModel = FileImportModel(sfsCredentials)
        fileImportModel.init().subscribe()
        fileImportModel.importDir(directoryToImport, FileTypes.UNKNOWN).test()
                .assertComplete()
                .assertNoErrors()

        //  assert
        assertEquals("Single file imported", 2, sfs().extListFiles().size)
        sfs().extListFiles().forEach { file ->
            run {
                assertEquals("File type", FileTypes.UNKNOWN, sfs().getFileMeta(file)?.getFileType())
            }
        }

    }

}