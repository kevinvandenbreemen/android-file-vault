package com.vandenbreemen.secretcamera.mvp.impl

import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
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
        addForDisposal(model.getNotes().subscribe({ notes -> mainMenuView.gotoNotesList(model.credentials, ArrayList<String>(notes)) }))
    }

    override fun openActions() {
        mainMenuView.openActions(model.copyCredentials())
    }

    override fun openProjects() {
        mainMenuView.goToProjects(model.copyCredentials())
    }

    override fun takeNote() {
        mainMenuView.gotoTakeNote(model.copyCredentials())
    }

    override fun takePicture() {
        mainMenuView.showError(ApplicationError("Currently not Supported"))
        return

        //  At the moment Camera Kit is having difficulty with race conditions.  Taking pictures is currently not the
        //  core purpose of this app...
        //mainMenuView.gotoTakePicture(model.copyCredentials())
    }

    override fun openGallery() {
        mainMenuView.gotoGallery(model.copyCredentials())
    }
}