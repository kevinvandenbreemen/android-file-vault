package com.vandenbreemen.secretcamera.mvp.impl.notes

import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Presenter
import com.vandenbreemen.secretcamera.mvp.notes.NoteDetailsPresenter
import com.vandenbreemen.secretcamera.mvp.notes.NoteDetailsView
import com.vandenbreemen.standardandroidlogging.log.SystemLog

/**
 * Created by kevin on 23/03/18.
 */
class NoteDetailsPresenterImpl(private val model: NoteDetailsModel, private val view: NoteDetailsView) : Presenter<NoteDetailsModel, NoteDetailsView>(model, view), NoteDetailsPresenter {
    override fun onEdit() {
        this.model.startEditing()
        this.view.enableEdit()
    }

    override fun setupView() {
        view.setNoteContent(model.getNote().content)
        view.setNoteTitle(model.getNote().title)
    }

    override fun onOk() {
        if (model.isEditing()) {
            val note = view.getNoteOnUI()
            addForDisposal(
                    model.updateNote(note.title, note.content).subscribe(
                            { view.close(model.copyCredentials()) },
                            { error ->
                                SystemLog.get().error(NoteDetailsPresenterImpl::class.java.simpleName, "Error updating note", error)
                                view.showError(if (error is ApplicationError) error as ApplicationError
                                else ApplicationError("Unknown error"))
                            }
                    )
            )
            return
        }
        view.close(model.copyCredentials())
    }

}