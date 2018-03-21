package com.vandenbreemen.secretcamera.mvp.impl

import android.util.Log
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Presenter
import com.vandenbreemen.secretcamera.mvp.notes.TakeNewNotePresenter
import com.vandenbreemen.secretcamera.mvp.notes.TakeNewNoteView

/**
 * <h2>Intro
 *
 * <h2>Other Details
 * @author kevin
 */
class TakeNewNotePresenterImpl(val view: TakeNewNoteView, val model: TakeNewNoteModel) :
        Presenter<TakeNewNoteModel, TakeNewNoteView>(model, view),
        TakeNewNotePresenter {

    override fun provideNoteDetails(title: String?, note: String?) {
        try {
            model.submitNewNote(title ?: "", note ?: "")
                    .subscribe({ _ ->
                        Log.d("TakeNewNotePresenter", "New note taken - ${Thread.currentThread()}")
                        view.onNoteSucceeded("New note created")
                        view.close()
                    },
                            { failure ->
                                Log.e("UnexpectedError", "Error storing new note", failure)
                                view.showError(ApplicationError("Unexpected error"))
                                view.close()
                            }
                    )
        } catch (error: ApplicationError) {
            view.showError(error)
        }
    }

    override fun onCancel() {
        view.close()
    }


}