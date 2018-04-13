package com.vandenbreemen.mobilesecurestorage.android.mvp.importfiles

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.api.SecureFileSystemInteractor
import com.vandenbreemen.mobilesecurestorage.file.api.getSecureFileSystemInteractor
import com.vandenbreemen.mobilesecurestorage.file.getFileImporter
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.computation
import java.io.File

/**
 * Created by kevin on 04/04/18.
 */
class FileImportModel(credentials: SFSCredentials) : Model(credentials) {

    /**
     *
     */
    private val fileSystemInteractor = FileSystemInteractorImpl()

    private val fileLoader = getFileImporter()
    lateinit var secureFileSystemInteractor: SecureFileSystemInteractor

    override fun onClose() {

    }

    override fun setup() {
        this.secureFileSystemInteractor = getSecureFileSystemInteractor(sfs)
    }

    fun importDir(directoryToImport: File): Observable<Int> {
        return fileSystemInteractor.listFiles(directoryToImport).flatMapObservable { files: List<File>? ->
            Observable.create(ObservableOnSubscribe<Int> { emitter ->
                files?.let {
                    var count = 0
                    it.forEach({ fileToImport ->

                        val bytes = fileLoader.loadFile(fileToImport)
                        secureFileSystemInteractor.importToFile(bytes, fileLoader.getFilenameToUseWhenImporting(fileToImport))

                        count++
                        emitter.onNext(count)
                    })
                    emitter.onComplete()
                } ?: run { emitter.onError(ApplicationError("Unknown error importing files")) }
            }).observeOn(mainThread()).subscribeOn(computation())
        }

    }

    fun countFiles(directoryToImport: File): Single<Int> {
        return fileSystemInteractor.countFiles(directoryToImport)
    }
}