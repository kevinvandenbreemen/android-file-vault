package com.vandenbreemen.secretcamera.mvp.impl

import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Presenter
import com.vandenbreemen.secretcamera.mvp.SFSMenuContract

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
class SFSMainMenuPresenterImpl(val model:SFSMainMenuModel, val mainMenuView: SFSMenuContract.SFSMainMenuView) :
        Presenter<SFSMainMenuModel, SFSMenuContract.SFSMainMenuView>(model, mainMenuView) , SFSMenuContract.SFSMainMenuPresenter {
    override fun setupView() {

    }

    override fun viewNotes() {
        model.getNotes().subscribe({notes->mainMenuView.gotoNotesList(model.credentials, ArrayList<String>(notes))})
    }

    override fun takeNote() {
        mainMenuView.gotoTakeNote()
    }

    override fun takePicture() {
        mainMenuView.gotoTakePicture()
    }
}