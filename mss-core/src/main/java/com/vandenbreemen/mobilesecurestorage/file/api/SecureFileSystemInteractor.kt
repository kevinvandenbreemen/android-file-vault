package com.vandenbreemen.mobilesecurestorage.file.api

import com.vandenbreemen.mobilesecurestorage.file.FileMeta
import com.vandenbreemen.mobilesecurestorage.file.ImportedFileData
import com.vandenbreemen.mobilesecurestorage.log.SystemLog
import com.vandenbreemen.mobilesecurestorage.log.e
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.security.crypto.getFileMeta
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.mobilesecurestorage.security.crypto.setFileMetadata
import java.io.Serializable

/**
 * <h2>Intro</h2>
 * Basic interaction for SFS
 * <h2>Other Details</h2>
 * @author kevin
 */
interface SecureFileSystemInteractor {

    @Throws(ApplicationError::class)
    fun importToFile(fileDataToImport: ImportedFileData, fileName: String, fileType: FileType?): Boolean

    fun save(obj: Serializable, fileName: String, fileType: FileType)
    fun load(fileName: String, fileTypes: FileTypes): Serializable?
    fun delete(fileName: String)
    fun delete(fileNames: List<String>)

}

private class SecureFileSystemInteractorImpl(private val secureFileSystem: SecureFileSystem) : SecureFileSystemInteractor {
    override fun save(obj: Serializable, fileName: String, fileTypes: FileType) {
        secureFileSystem.storeObject(fileName, obj)
        secureFileSystem.setFileMetadata(fileName, FileMeta(fileTypes))
    }

    override fun load(fileName: String, fileTypes: FileTypes): Serializable? {
        secureFileSystem.getFileMeta(fileName)?.let {
            if (fileTypes.equals(it.getFileType())) {
                return secureFileSystem.loadAndCacheFile(fileName) as Serializable
            }
        }
        return null
    }

    override fun importToFile(fileDataToImport: ImportedFileData, fileName: String, fileType: FileType?): Boolean {
        if (secureFileSystem.exists(fileName)) {
            SystemLog.get().e("SFSInteractor", "File already exists.  Cannot overwrite existing file.", Throwable())
            return false
        }
        secureFileSystem.storeObject(fileName, fileDataToImport)
        fileType?.let { type -> secureFileSystem.setFileMetadata(fileName, FileMeta(type)) }
        return true
    }

    override fun delete(fileName: String) {
        secureFileSystem.deleteFile(fileName)
    }

    override fun delete(fileNames: List<String>) {
        this.secureFileSystem.deleteFiles(*fileNames.toTypedArray())
    }
}

fun getSecureFileSystemInteractor(secureFileSystem: SecureFileSystem): SecureFileSystemInteractor {
    return SecureFileSystemInteractorImpl(secureFileSystem)
}