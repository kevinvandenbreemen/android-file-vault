package com.vandenbreemen.secretcamera.mvp.impl

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.secretcamera.api.Note
import java.util.*

/**
 * <h2>Intro</h2>
 * Model for taking a new note
 * <h2>Other Details</h2>
 * @author kevin
 */
class TakeNewNoteModel(credentials: SFSCredentials) {

    private val fileSystem: SecureFileSystem = object : SecureFileSystem(credentials.fileLocation) {
        override fun getPassword(): SecureString {
            return credentials.password
        }
    }

    fun submitNewNote(title: String, content: String) {
        if (title.isBlank()) {
            throw ApplicationError("Title is required")
        }
        if (content.isBlank()) {
            throw ApplicationError("Note content is required")
        }

        fileSystem.storeObject("newnote_" + Date() + "_" + System.nanoTime() % 1000, Note(title, content))
    }
}