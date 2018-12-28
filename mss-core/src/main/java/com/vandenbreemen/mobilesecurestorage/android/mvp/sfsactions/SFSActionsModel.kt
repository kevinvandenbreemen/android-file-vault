package com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.patterns.ProgressListener
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers.computation

class SFSActionsModel(credentials: SFSCredentials): Model(credentials) {

    override fun onClose() {

    }

    override fun setup() {

    }

    fun changePassword(currentPassword: String, newPassword: String, reEnterNewPassword: String, progress: ProgressListener<Long>): Completable {
        return Completable.create {subscriber ->

            if(!sfs.testPassword(SecureString.fromPassword(currentPassword))){
                subscriber.onError(ApplicationError("Current password is not correct"))
                return@create
            }

            sfs.changePassword(progress, SecureFileSystem.generatePassword(SecureString.fromPassword(newPassword)))
            subscriber.onComplete()
        }.subscribeOn(computation())
    }


}
