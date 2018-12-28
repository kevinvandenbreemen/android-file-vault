package com.vandenbreemen.secretcamera.mvp

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.PresenterContract
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.View

/**
 * <h2>Intro</h2>
 * Contract/participants in the main menu (after SFS loaded)
 * <h2>Other Details</h2>
 * @author kevin
 */
class SFSMenuContract{
    interface SFSMainMenuPresenter:PresenterContract{
        fun takePicture()
        fun takeNote()
        fun viewNotes()
        fun openGallery()
        fun openProjects()
        fun openActions()
    }

    /**
     * Main menu view
     */
    interface SFSMainMenuView:View{
        fun gotoTakePicture(credentials: SFSCredentials)
        fun gotoTakeNote(credentials: SFSCredentials)
        fun gotoNotesList(credentials:SFSCredentials, strings:ArrayList<String>)
        fun gotoGallery(credentials: SFSCredentials)
        fun goToProjects(credentials: SFSCredentials)
        fun openActions(credentials: SFSCredentials)
    }
}


