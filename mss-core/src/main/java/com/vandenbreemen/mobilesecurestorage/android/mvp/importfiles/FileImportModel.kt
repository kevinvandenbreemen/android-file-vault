package com.vandenbreemen.mobilesecurestorage.android.mvp.importfiles

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
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

    fun importDir(directoryToImport: File): Single<Unit> {
        return fileSystemInteractor.listFiles(directoryToImport).flatMap { files: List<File>? ->
            Single.create(SingleOnSubscribe<Unit> { emitter ->
                files?.let {
                    it.forEach({ fileToImport -> sfs.importFile(fileToImport, fileToImport.name) })
                    emitter.onSuccess(Unit)
                } ?: run { emitter.onError(ApplicationError("Unknown error importing files")) }
            }).observeOn(computation()).subscribeOn(mainThread())
        }

    }
}