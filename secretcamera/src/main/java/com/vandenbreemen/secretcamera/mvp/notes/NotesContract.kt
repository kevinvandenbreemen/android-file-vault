package com.vandenbreemen.secretcamera.mvp.notes

import com.vandenbreemen.mobilesecurestorage.message.ApplicationError

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
interface TakeNewNotePresenter {

    fun provideNoteDetails(title: String?, note: String?)

    fun onCancel()

}

interface TakeNewNoteView {
    fun showError(error: ApplicationError)

    fun onNoteSucceeded(message: String)

    fun close()
}