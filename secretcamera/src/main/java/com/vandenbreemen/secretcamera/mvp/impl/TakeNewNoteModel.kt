package com.vandenbreemen.secretcamera.mvp.impl

import android.util.Log
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.log.SystemLog
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.secretcamera.api.Note
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.io
import java.util.*

/**
 * <h2>Intro</h2>
 * Model for taking a new note
 * <h2>Other Details</h2>
 * @author kevin
 */
class TakeNewNoteModel(private val credentials: SFSCredentials) {

    private lateinit var fileSystem: SecureFileSystem

    fun initializeAsynchronously(): Single<Unit> {
        return Single.create(SingleOnSubscribe<Unit> {
            try {
                fileSystem = object : SecureFileSystem(credentials.fileLocation) {
                    override fun getPassword(): SecureString {
                        return credentials.password
                    }
                }
                it.onSuccess(Unit)
            } catch (exception: Exception) {
                SystemLog.get().error("Failed to load SFS", exception)
                it.onError(exception)
            }
        }).subscribeOn(io()).observeOn(mainThread())
    }

    fun submitNewNote(title: String, content: String): Single<Unit> {
        if (title.isBlank()) {
            throw ApplicationError("Title is required")
        }
        if (content.isBlank()) {
            throw ApplicationError("Note content is required")
        }

        return Single.create(SingleOnSubscribe<Unit> {
            try {
                fileSystem.storeObject("newnote_" + Date() + "_" + System.nanoTime() % 1000, Note(title, content))
                it.onSuccess(Unit)
                Log.d("TakeNewNote", "New note stored successfully - ${Thread.currentThread()}")
            } catch (exc: Exception) {
                Log.e("TakeNewNoteFailure", "Error storing note", exc)
                it.onError(exc)
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}