package com.vandenbreemen.mobilesecurestorage.android.mvp.importfiles

import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import java.io.File

/**
 * Created by kevin on 04/04/18.
 */
class FileSystemInteractorImpl : FileSystemInteractor {
    override fun listFiles(directory: File): Single<List<File>> {
        return Single.create(SingleOnSubscribe<List<File>> {
            if (isNotDirectory(directory)) {
                it.onError(ApplicationError("${directory.absolutePath} does not exist or is not a directory"))
            } else {
                it.onSuccess(directory.listFiles().asList())
            }
        }).observeOn(io()).subscribeOn(mainThread())
    }

    override fun countFiles(directory: File): Single<Int> {
        return Single.create(SingleOnSubscribe<Int> {
            if (isNotDirectory(directory)) {
                it.onError(ApplicationError("${directory.absolutePath} does not exist or is not a directory"))
            } else {
                it.onSuccess(directory.listFiles().size)
            }
        }).observeOn(mainThread()).subscribeOn(io())
    }

    private fun isNotDirectory(directory: File) = !directory.exists() || !directory.isDirectory
}