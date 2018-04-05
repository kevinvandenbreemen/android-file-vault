package com.vandenbreemen.mobilesecurestorage.android.mvp.importfiles

import android.os.Environment
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

/**
 * Created by kevin on 04/04/18.
 */
@RunWith(RobolectricTestRunner::class)
class FileSystemInteractorImplTest {

    lateinit var directory: File

    @Before
    fun setup() {
        directory = File(Environment.getExternalStorageDirectory().toString() + File.separator + "test")
        directory.mkdir()

        for (i in 1..4) {
            val file = File(directory.absolutePath + File.separator + "file$i")
            file.createNewFile()
        }


    }

    @Test
    fun shouldListFiles() {
        val interactor: FileSystemInteractor = FileSystemInteractorImpl()
        interactor.listFiles(directory).test()
                .assertComplete()
                .assertValue({ it.size == 4 })
    }

    @Test
    fun shouldReturnErrorIfNotDirectory() {
        val notADirectory = File(directory.absolutePath + File.separator + "file_2")
        notADirectory.createNewFile()
        val interactor: FileSystemInteractor = FileSystemInteractorImpl()
        interactor.listFiles(notADirectory).test()
                .assertError(ApplicationError::class.java)
                .assertNoValues()
    }

    @Test
    fun shouldReturnErrorIfFileDoesNotExist() {
        val interactor: FileSystemInteractor = FileSystemInteractorImpl()
        interactor.listFiles(File(directory.absolutePath + File.separator + "file_2")).test()
                .assertError(ApplicationError::class.java)
                .assertNoValues()
    }

}