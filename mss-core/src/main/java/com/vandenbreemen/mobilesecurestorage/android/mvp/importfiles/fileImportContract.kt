package com.vandenbreemen.mobilesecurestorage.android.mvp.importfiles

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import io.reactivex.Single
import java.io.File

interface FileSystemInteractor {

    /**
     * Provide observable to listing all files
     */
    fun listFiles(directory: File): Single<List<File>>

}

interface FileImportPresenter {

    fun import(directory: File)

}

interface FileImportView {

    fun showTotalFiles(totalFiles: Int)

    fun updateProgress(numberOfFilesImported: Int)

    fun done(sfsCredentials: SFSCredentials)

}