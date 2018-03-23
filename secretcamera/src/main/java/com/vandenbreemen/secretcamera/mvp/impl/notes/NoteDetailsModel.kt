package com.vandenbreemen.secretcamera.mvp.impl.notes

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import com.vandenbreemen.secretcamera.api.Note

/**
 * Created by kevin on 22/03/18.
 */
class NoteDetailsModel(credentials: SFSCredentials, private val noteFilename:String) : Model(credentials) {
    override fun onClose() {

    }

    fun getNote(): Note {
        return sfs.loadFile(noteFilename) as Note
    }
}