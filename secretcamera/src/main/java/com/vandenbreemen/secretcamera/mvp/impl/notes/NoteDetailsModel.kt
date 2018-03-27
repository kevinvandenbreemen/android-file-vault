package com.vandenbreemen.secretcamera.mvp.impl.notes

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import com.vandenbreemen.secretcamera.api.Note
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.computation

/**
 * Created by kevin on 22/03/18.
 */
class NoteDetailsModel(credentials: SFSCredentials, private val noteFilename:String) : Model(credentials) {

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
            sfs.storeObject(noteFilename, newNote)
            it.onSuccess(Unit)
        }).subscribeOn(computation()).observeOn(mainThread())
    }

    fun isEditing(): Boolean {
        return this.editing
    }

    private var editing: Boolean = false

    fun startEditing() {
        this.editing = true
    }
}