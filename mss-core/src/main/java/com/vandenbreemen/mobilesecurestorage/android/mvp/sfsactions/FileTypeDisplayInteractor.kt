package com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions

import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem

class FileTypeDisplayInteractor(private val sfs: SecureFileSystem) {

    fun iconFor(fileName: String): FileTypeIcon {
        return CoreFileTypeIcons.UNKNOWN
    }

}
