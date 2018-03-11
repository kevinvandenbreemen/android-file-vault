package com.vandenbreemen.secretcamera.mvp

/**
 * <h2>Intro</h2>
 * Contract/participants in the main menu (after SFS loaded)
 * <h2>Other Details</h2>
 * @author kevin
 */
class SFSMenuContract{
    interface SFSMainMenuPresenter{
        fun takePicture()


    }

    /**
     * Main menu view
     */
    interface SFSMainMenuView{
        fun gotoTakePicture()

    }
}


