package com.vandenbreemen.secretcamera.mvp.impl.notes

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.FileMeta
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import com.vandenbreemen.mobilesecurestorage.security.crypto.setFileMetadata
import com.vandenbreemen.secretcamera.api.Note
import com.vandenbreemen.secretcamera.mvp.impl.NoteFileTypes
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.computation
import java.util.*

/**
 * Created by kevin on 22/03/18.
 */
class NoteDetailsModel(credentials: SFSCredentials, private val noteFilename:String) : Model(credentials) {

    private var editing: Boolean = false

    private lateinit var note:Note

    override fun setup():Unit{
        this.note = sfs.loadFile(noteFilename) as Note
    }

    override fun onClose() {

    }

    fun getNote(): Note {
        return note
    }

    fun updateNote(title: String, content: String): Single<Unit> {
        return Single.create(SingleOnSubscribe<Unit> {
            val newNote = Note(title, content)
            val newNoteFileName = title + " " + Date() + "_" + System.nanoTime() % 1000
            sfs.storeObject(newNoteFileName, newNote)
            sfs.setFileMetadata(newNoteFileName, FileMeta(NoteFileTypes.SIMPLE_NOTE))
            sfs.deleteFile(noteFilename)
            it.onSuccess(Unit)
        }).subscribeOn(computation()).observeOn(mainThread())
    }

    fun isEditing(): Boolean {
        return this.editing
    }


    fun startEditing() {
        this.editing = true
    }
}