package com.vandenbreemen.secretcamera.mvp

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

    }

    /**
     * Main menu view
     */
    interface SFSMainMenuView:View{
        fun gotoTakePicture()
        fun gotoTakeNote()
    }
}


