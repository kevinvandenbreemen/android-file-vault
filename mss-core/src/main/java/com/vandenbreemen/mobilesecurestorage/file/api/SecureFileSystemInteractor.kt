package com.vandenbreemen.mobilesecurestorage.file.api

import com.vandenbreemen.mobilesecurestorage.file.ImportedFileData
import com.vandenbreemen.mobilesecurestorage.file.IndexedFile
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem

/**
 * <h2>Intro</h2>
 * Basic interaction for SFS
 * <h2>Other Details</h2>
 * @author kevin
 */
interface SecureFileSystemInteractor {

    @Throws(ApplicationError::class)
    fun importToFile(fileDataToImport: ImportedFileData, fileName: String)

}

private class SecureFileSystemInteractorImpl(private val secureFileSystem: IndexedFile) : SecureFileSystemInteractor {
    override fun importToFile(fileDataToImport: ImportedFileData, fileName: String) {
        if (secureFileSystem.exists(fileName)) {
            throw ApplicationError("File $fileName already exists.  Cannot overwrite existing file.")
        }
        secureFileSystem.storeObject(fileName, fileDataToImport)
    }

}

fun getSecureFileSystemInteractor(secureFileSystem: SecureFileSystem): SecureFileSystemInteractor {
    return SecureFileSystemInteractorImpl(secureFileSystem)
}