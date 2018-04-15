package com.vandenbreemen.mobilesecurestorage.file.api

import com.vandenbreemen.mobilesecurestorage.file.FileMeta
import com.vandenbreemen.mobilesecurestorage.file.ImportedFileData
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.mobilesecurestorage.security.crypto.setFileMetadata

/**
 * <h2>Intro</h2>
 * Basic interaction for SFS
 * <h2>Other Details</h2>
 * @author kevin
 */
interface SecureFileSystemInteractor {

    @Throws(ApplicationError::class)
    fun importToFile(fileDataToImport: ImportedFileData, fileName: String, fileType: FileType?)

}

private class SecureFileSystemInteractorImpl(private val secureFileSystem: SecureFileSystem) : SecureFileSystemInteractor {
    override fun importToFile(fileDataToImport: ImportedFileData, fileName: String, fileType: FileType?) {
        if (secureFileSystem.exists(fileName)) {
            throw ApplicationError("File $fileName already exists.  Cannot overwrite existing file.")
        }
        secureFileSystem.storeObject(fileName, fileDataToImport)
        fileType?.let { type -> secureFileSystem.setFileMetadata(fileName, FileMeta(type)) }
    }

}

fun getSecureFileSystemInteractor(secureFileSystem: SecureFileSystem): SecureFileSystemInteractor {
    return SecureFileSystemInteractorImpl(secureFileSystem)
}