package com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions

import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem

class FileTypeDisplayInteractor(private val sfs: SecureFileSystem) {

    fun iconFor(fileName: String): FileTypeIcon {

        var fileTypeIcon: FileTypeIcon = CoreFileTypeIcons.UNKNOWN

        sfs.getDetails(fileName)?.let { details ->

            details.fileMeta?.let { metadata ->

                metadata.getFileType()?.let { fileType ->
                    if (fileType == FileTypes.SYSTEM) {
                        fileTypeIcon = CoreFileTypeIcons.SYSTEM
                    }
                }

            }

            CoreFileTypeIcons.UNKNOWN
        }

        return fileTypeIcon
    }

}
