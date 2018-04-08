@file:JvmName("FileImporterJavaInteractor")
package com.vandenbreemen.mobilesecurestorage.file

import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.security.Bytes
import java.io.File

/**
 * <h2>Intro</h2>
 * Specialists in importing file data
 * <h2>Other Details</h2>
 * @author kevin
 */
interface FileLoader {

    @Throws(ApplicationError::class)
    fun loadFile(fileToImport: File): ImportedFileData

    fun getFilenameToUseWhenImporting(file: File): String
}

private class FileLoaderImpl : FileLoader {
    override fun getFilenameToUseWhenImporting(file: File): String {
        return file.name
    }

    override fun loadFile(fileToImport: File): ImportedFileData {
        if (fileToImport.isDirectory) {
            throw ApplicationError("Cannot import a directory")
        }
        if (!fileToImport.exists()) {
            throw ApplicationError("Cannot import non-existent file ${fileToImport.name}")
        }
        return ImportedFileData(Bytes.loadBytesFromFile(fileToImport))
    }

}

fun getFileImporter(): FileLoader {
    return FileLoaderImpl()
}