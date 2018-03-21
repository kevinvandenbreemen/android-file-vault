package com.vandenbreemen.secretcamera.mvp

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.PresenterContract
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.View
import com.vandenbreemen.secretcamera.StringSelectorWorkflow

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
    }

    /**
     * Main menu view
     */
    interface SFSMainMenuView:View{
        fun gotoTakePicture()
        fun gotoTakeNote()
        fun gotoNotesList(credentials:SFSCredentials, strings:ArrayList<String>)
    }
}


