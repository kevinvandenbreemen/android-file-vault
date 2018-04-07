package com.vandenbreemen.mobilesecurestorage.file

import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import java.io.File

/**
 * <h2>Intro</h2>
 * Specialists in importing file data
 * <h2>Other Details</h2>
 * @author kevin
 */
interface FileImporter {

    @Throws(ApplicationError::class)
    fun importFile(fileToImport: File): ImportedFileData
}

private class FileImporterImpl : FileImporter {
    override fun importFile(fileToImport: File): ImportedFileData {
        if (fileToImport.isDirectory) {
            throw ApplicationError("Cannot import a directory")
        }
        if (!fileToImport.exists()) {
            throw ApplicationError("Cannot import non-existent file ${fileToImport.name}")
        }
        return ImportedFileData.loadFileFromDisk(fileToImport)
    }

}

fun getFileImporter(): FileImporter {
    return FileImporterImpl()
}