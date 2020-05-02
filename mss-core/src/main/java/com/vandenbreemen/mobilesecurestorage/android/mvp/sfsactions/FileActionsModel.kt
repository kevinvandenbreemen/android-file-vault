package com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions

import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.schedulers.Schedulers.computation

/**
 * Application logic for performing actions on a specific file
 * @author kevin
 */
class FileActionsModel(private val interactor: FileActionsInteractor) {

    fun rename(newName: String): Single<Boolean> {
        return Single.create(SingleOnSubscribe<Boolean> { emitter ->
            try {
                interactor.rename(newName)
                emitter.onSuccess(true)
            } catch (err: ApplicationError) {
                emitter.onError(err)
            }
        }).subscribeOn(computation())
    }

}