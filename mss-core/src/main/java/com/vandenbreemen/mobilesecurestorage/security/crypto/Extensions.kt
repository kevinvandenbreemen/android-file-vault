package com.vandenbreemen.mobilesecurestorage.security.crypto

import com.vandenbreemen.mobilesecurestorage.file.FileMeta
import com.vandenbreemen.mobilesecurestorage.file.IndexedFile
import com.vandenbreemen.mobilesecurestorage.file.api.FileType
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import java.io.Serializable
import java.util.function.Supplier

//  SFS extensions in Kotlin

/**
 * @deprecated  Please use sfs.setFileType()
 * Specificy metadata for the given file
 */
@Deprecated(message = "Please use sfs.setFileType()")
fun SecureFileSystem.setFileMetadata(fileName: String, metadata: FileMeta) {
    this.setFileType(fileName, metadata.getFileType())
}

/**
 * @deprecated  Please use sfs.getDetails(fileName)
 * Gets the file metadata for the given file
 */
fun SecureFileSystem.getFileMeta(fileName: String): FileMeta? {
    return this.getDetails(fileName).fileMeta
}

fun SecureFileSystem.getFileMeta(fileName: String, provider: Supplier<FileMeta>): FileMeta {

    var meta = getFileMeta(fileName)
    meta ?: kotlin.run {
        meta = provider.get()
        setFileMetadata(fileName, meta!!)
        return@getFileMeta meta!!
    }

    return meta!!
}

/**
 * @deprecated  Please integrate special files etc into the FAT, not into extensions
 * List files, ignoring any special files required to make the extensions work
 */
fun SecureFileSystem.extListFiles(): List<String> {
    return this.listFiles()
}

fun SecureFileSystem.listFiles(vararg fileTypes: FileType): List<String> {
    return this.extListFiles().filter {
        fileTypes.contains(getFileMeta(it)?.getFileType())
    }
}