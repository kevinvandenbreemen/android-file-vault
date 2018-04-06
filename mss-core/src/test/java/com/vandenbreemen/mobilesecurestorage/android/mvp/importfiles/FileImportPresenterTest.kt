package com.vandenbreemen.mobilesecurestorage.android.mvp.importfiles

import android.os.Environment
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

class FileImportTestView : FileImportView {

    var totalFiles: Int? = null

    val progressUpdates: ArrayList<Int> = ArrayList()


    override fun showTotalFiles(totalFiles: Int) {
        this.totalFiles = totalFiles
    }

    override fun updateProgress(numberOfFilesImported: Int) {
        progressUpdates.add(numberOfFilesImported)
    }

    override fun done(sfsCredentials: SFSCredentials) {

    }

    override fun onReadyToUse() {

    }

    override fun showError(error: ApplicationError) {

    }

}

/**
 * Created by kevin on 06/04/18.
 */
@RunWith(RobolectricTestRunner::class)
class FileImportPresenterTest {

    lateinit var directoryToImport: File

    lateinit var sfsFile: File

    lateinit var view: FileImportTestView

    lateinit var sut: FileImportPresenter

    @Before
    fun setup() {
        RxJavaPlugins.setIoSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }
        RxJavaPlugins.setComputationSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }
        directoryToImport = File(Environment.getExternalStorageDirectory().toString() + File.separator + "toImport")
        directoryToImport.mkdir()
        sfsFile = File(Environment.getExternalStorageDirectory().toString() + File.separator + "sfs")

        view = FileImportTestView()
        sut = FileImportPresenterImpl(FileImportModel(SFSCredentials(sfsFile, createPassword())), view)
        sut.start()
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
    fun shouldShowTotalFilesToBeImported() {
        //  Arrange
        var file = File(directoryToImport.absolutePath + File.separator + "file1")
        file.createNewFile()
        file = File(directoryToImport.absolutePath + File.separator + "file2")
        file.createNewFile()
        file = File(directoryToImport.absolutePath + File.separator + "file3")
        file.createNewFile()

        //  Act
        sut.import(directoryToImport)

        //  Assert
        assertEquals("Total files", 3, view.totalFiles ?: 0)
    }

    @Test
    fun shouldUpdateProgressAsFilesAreImported() {
        //  Arrange
        var file = File(directoryToImport.absolutePath + File.separator + "file1")
        file.createNewFile()
        file = File(directoryToImport.absolutePath + File.separator + "file2")
        file.createNewFile()
        file = File(directoryToImport.absolutePath + File.separator + "file3")
        file.createNewFile()

        //  Act
        sut.import(directoryToImport)

        //  Assert
        assertEquals("Progress Updates", 3, view.progressUpdates.size)
    }

}