@file:JvmName("FileImporterJavaInteractor")
package com.vandenbreemen.mobilesecurestorage.file

import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.message.MSSRuntime
import com.vandenbreemen.mobilesecurestorage.security.Bytes
import com.vandenbreemen.standardandroidlogging.log.SystemLog
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
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

    /**
     * Load the given file on the IO scheduler, returning a single rxandroid handler
     */
    fun loadFileReactive(fileToImport: File): Single<ImportedFileData>

    fun getFilenameToUseWhenImporting(file: File): String

    fun getAlternateNameForFileImport(file: File, indexedFile: IndexedFile): String
}

private class FileLoaderImpl : FileLoader {
    override fun getAlternateNameForFileImport(file: File, indexedFile: IndexedFile): String {
        if (!indexedFile.exists(getFilenameToUseWhenImporting(file))) {
            return getFilenameToUseWhenImporting(file)
        }
        val filename = getFilenameToUseWhenImporting(file)
        var index = 0
        while (indexedFile.exists(filename)) {
            val altFilename = "${filename}_${++index}"
            if (!indexedFile.exists(altFilename)) {
                return altFilename
            }
        }

        throw MSSRuntime("Impossible condition - infinitely large file system")
    }

    override fun loadFileReactive(fileToImport: File): Single<ImportedFileData> {
        return Single.create(SingleOnSubscribe<ImportedFileData> { subscriber ->
            try {
                subscriber.onSuccess(loadFile(fileToImport))
            } catch (ex: ApplicationError) {
                SystemLog.get().error("FileLoader", "Failed to get bytes for ${fileToImport.absolutePath}", ex)
                subscriber.onError(ex)
            }
        }).observeOn(io()).subscribeOn(mainThread())
    }

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