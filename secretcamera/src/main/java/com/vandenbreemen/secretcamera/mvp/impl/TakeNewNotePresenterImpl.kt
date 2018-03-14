package com.vandenbreemen.secretcamera.mvp.impl

import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.mvp.notes.TakeNewNotePresenter
import com.vandenbreemen.secretcamera.mvp.notes.TakeNewNoteView

/**
 * <h2>Intro
 *
 * <h2>Other Details
 * @author kevin
 */
class TakeNewNotePresenterImpl(val view: TakeNewNoteView, val model: TakeNewNoteModel) : TakeNewNotePresenter {
    override fun close() {
        view.close()
    }

    override fun provideNoteDetails(title: String?, note: String?) {
        try {
            model.submitNewNote(title ?: "", note ?: "")
            view.onNoteSucceeded("New note created")
        } catch (error: ApplicationError) {
            view.showError(error)
        }
    }

    override fun onCancel() {
        view.close()
    }


}