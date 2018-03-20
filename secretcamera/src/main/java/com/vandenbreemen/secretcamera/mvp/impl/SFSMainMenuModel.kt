package com.vandenbreemen.secretcamera.mvp.impl

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.log.SystemLog
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.extListFiles
import com.vandenbreemen.mobilesecurestorage.security.crypto.getFileMeta
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
class SFSMainMenuModel(private val credentials: SFSCredentials) {

    lateinit var sfs: SecureFileSystem

    /**
     * Initialize the menu with the given credentials
     */
    fun init(): Single<Unit> {
        return Single.create(SingleOnSubscribe<Unit> {
            try {
                sfs = object : SecureFileSystem(credentials.fileLocation) {
                    override fun getPassword(): SecureString {
                        return credentials.password
                    }
                }
                it.onSuccess(Unit)
            } catch (exc: Exception) {
                SystemLog.get().error("SFSMainMenu", "Failed to load", exc)
                it.onError(exc)
            }
        }).subscribeOn(io()).observeOn(mainThread())
    }

    fun getNotes(): Single<List<String>> {
        return Single.create(SingleOnSubscribe<List<String>> {
            it.onSuccess(sfs.extListFiles().filter {
                NoteFileTypes.SIMPLE_NOTE.equals(sfs.getFileMeta(it)?.getFileType() ?: null)
            })
        }).subscribeOn(io()).observeOn(mainThread())
    }

}