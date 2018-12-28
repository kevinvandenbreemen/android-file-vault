package com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.patterns.ProgressListener
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.schedulers.Schedulers.computation

class SFSActionsModel(credentials: SFSCredentials): Model(credentials) {

    override fun onClose() {

    }

    override fun setup() {

    }

    fun changePassword(currentPassword: String, newPassword: String, reEnterNewPassword: String, progress: ProgressListener<Long>): Single<SecureString> {
        return Single.create(SingleOnSubscribe<SecureString> {
            subscriber ->

            if(!sfs.testPassword(SecureString.fromPassword(currentPassword))){
                subscriber.onError(ApplicationError("Current password is not correct"))
                return@SingleOnSubscribe
            }

            val updatedPassword = SecureFileSystem.generatePassword(SecureString.fromPassword(newPassword))
            sfs.changePassword(progress, updatedPassword)
            subscriber.onSuccess(updatedPassword)
        }).subscribeOn(computation())
    }


}
