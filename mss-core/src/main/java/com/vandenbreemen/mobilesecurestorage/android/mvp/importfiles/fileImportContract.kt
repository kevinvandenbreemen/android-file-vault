package com.vandenbreemen.mobilesecurestorage.android.mvp.importfiles

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.PresenterContract
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.View
import io.reactivex.Single
import java.io.File

interface FileSystemInteractor {

    /**
     * Provide observable to listing all files
     */
    fun listFiles(directory: File): Single<List<File>>

}

interface FileImportPresenter : PresenterContract {

    fun import(directory: File)

}

interface FileImportView : View {

    fun showTotalFiles(totalFiles: Int)

    fun updateProgress(numberOfFilesImported: Int)

    fun done(sfsCredentials: SFSCredentials)

}