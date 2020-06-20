package com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions

import com.vandenbreemen.mobilesecurestorage.file.api.FileInfo
import com.vandenbreemen.mobilesecurestorage.file.api.SecureFileSystemInteractor
import com.vandenbreemen.mobilesecurestorage.file.api.SecureFileSystemInteractorFactory
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem

class FileDetailsInteractor(private val sfs: SecureFileSystem, private val fileName: String) {

    private lateinit var interactor: SecureFileSystemInteractor

    init {
        interactor = SecureFileSystemInteractorFactory.get(sfs)
    }

    fun getFileDetails(): FileInfo {
        return interactor.info(fileName)
    }


}
