package com.vandenbreemen.mobilesecurestorage.android.mvp.importfiles

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
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

    override fun onClose() {

    }

    override fun setup() {

    }

    fun importDir(directoryToImport: File): Observable<Int> {
        return fileSystemInteractor.listFiles(directoryToImport).flatMapObservable { files: List<File>? ->
            Observable.create(ObservableOnSubscribe<Int> { emitter ->
                files?.let {
                    var count = 0
                    it.forEach({ fileToImport ->
                        sfs.importFile(fileToImport)
                        count++
                        emitter.onNext(count)
                    })
                    emitter.onComplete()
                } ?: run { emitter.onError(ApplicationError("Unknown error importing files")) }
            }).observeOn(computation()).subscribeOn(mainThread())
        }

    }

    fun countFiles(directoryToImport: File): Single<Int> {
        return fileSystemInteractor.countFiles(directoryToImport)
    }
}