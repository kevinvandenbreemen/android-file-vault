package com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions

import com.vandenbreemen.mobilesecurestorage.file.api.FileInfo


/**
 *
 * @author kevin
 */
class FileDetailsModel(private val interactor: FileDetailsInteractor) {

    fun getFileInfo(): FileInfo {
        return interactor.getFileDetails()
    }

}