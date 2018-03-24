package com.vandenbreemen.secretcamera.mvp.impl.notes

import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Presenter
import com.vandenbreemen.secretcamera.mvp.notes.NoteDetailsPresenter
import com.vandenbreemen.secretcamera.mvp.notes.NoteDetailsView

/**
 * Created by kevin on 23/03/18.
 */
class NoteDetailsPresenterImpl(private val model: NoteDetailsModel, private val view: NoteDetailsView) : Presenter<NoteDetailsModel, NoteDetailsView>(model, view), NoteDetailsPresenter {
    override fun setupView() {
        view.setNoteContent(model.getNote().content)
        view.setNoteTitle(model.getNote().title)
    }

    override fun onOk() {

    }

}