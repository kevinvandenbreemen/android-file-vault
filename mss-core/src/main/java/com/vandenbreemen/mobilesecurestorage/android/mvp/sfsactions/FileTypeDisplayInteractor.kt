package com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions

import com.vandenbreemen.mobilesecurestorage.file.api.FileType
import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem

interface FileTypeDisplayExtension {

    fun iconFor(fileName: String, fileType: FileType): FileTypeIcon?

}

/**
 * Allows you to plug in your own icon types for file type display
 */
class FileTypeDisplayExtender {

    companion object {
        val shared: FileTypeDisplayExtender = FileTypeDisplayExtender()
    }

    private val extensions: MutableList<FileTypeDisplayExtension> = mutableListOf()

    private constructor()

    fun register(extension: FileTypeDisplayExtension) {
        extensions.add(extension)
    }

    fun iconFor(fileName: String, fileType: FileType): FileTypeIcon? {

        for (ex in extensions) {
            val result = ex.iconFor(fileName, fileType)
            if (result != null) {
                return result
            }
        }

        return null
    }

}

class FileTypeDisplayInteractor(private val sfs: SecureFileSystem) {

    private val fileTypeDisplayExtender: FileTypeDisplayExtender = FileTypeDisplayExtender.shared

    fun iconFor(fileName: String): FileTypeIcon {

        var fileTypeIcon: FileTypeIcon = CoreFileTypeIcons.UNKNOWN

        sfs.getDetails(fileName)?.let { details ->

            details.fileMeta?.let { metadata ->

                metadata.getFileType()?.let { fileType ->

                    val extendedType = fileTypeDisplayExtender.iconFor(fileName, fileType)
                    if (extendedType != null) {
                        return extendedType
                    }

                    if (fileType == FileTypes.SYSTEM) {
                        fileTypeIcon = CoreFileTypeIcons.SYSTEM
                    } else if (fileType == FileTypes.DATA) {
                        fileTypeIcon = CoreFileTypeIcons.DATA
                    }
                }

            }

            CoreFileTypeIcons.UNKNOWN
        }

        return fileTypeIcon
    }

}
