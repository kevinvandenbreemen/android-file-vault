package com.vandenbreemen.secretcamera.mvp.impl

import android.util.Log
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.FileMeta
import com.vandenbreemen.mobilesecurestorage.file.api.FileType
import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes
import com.vandenbreemen.mobilesecurestorage.log.SystemLog
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.secretcamera.api.Note
import com.vandenbreemen.secretcamera.api.SEC_CAM_BYTE
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*


enum class NoteFileTypes(override val firstByte: Byte, override val secondByte: Byte? = null) : FileType {

    SIMPLE_NOTE(SEC_CAM_BYTE, 1)

    ;

    //  Register these file types!
    companion object {
        init {
            FileTypes.registerFileTypes(values())
        }
    }

}

/**
 * <h2>Intro</h2>
 * Model for taking a new note
 * <h2>Other Details</h2>
 * @author kevin
 */
open class TakeNewNoteModel(private val credentials: SFSCredentials) : Model(credentials) {

    var saved = false

    override fun setup() {

    }

    override fun onClose() {

    }

    companion object {

        /**
         * Procedure for storing a new note to the SFS
         */
        fun storeNote(fileSystem: SecureFileSystem, title: String, content: String) {
            val fileName = "newnote_" + Date() + "_" + System.nanoTime() % 1000
            fileSystem.storeObject(fileName, Note(title, content))
            fileSystem.setFileType(fileName, NoteFileTypes.SIMPLE_NOTE)
        }
    }

    fun submitNewNote(title: String, content: String): Single<Unit> {

        if (saved) {
            SystemLog.get().error("TakeNewNoteModel", "Note was previously saved.  Doing nothing")
            return Single.just(Unit)
        }

        if (title.isBlank()) {
            throw ApplicationError("Title is required")
        }
        if (content.isBlank()) {
            throw ApplicationError("Note content is required")
        }

        return Single.create(SingleOnSubscribe<Unit> {
            try {

                storeNote(sfs, title, content)
                saved = true

                it.onSuccess(Unit)
                Log.d("TakeNewNote", "New note stored successfully - ${Thread.currentThread()}")
            } catch (exc: Exception) {
                Log.e("TakeNewNoteFailure", "Error storing note", exc)
                it.onError(exc)
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
    }


}