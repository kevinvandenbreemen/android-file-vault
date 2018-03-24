package com.vandenbreemen.secretcamera.mvp.impl.notes

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import com.vandenbreemen.secretcamera.api.Note

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
}