package com.vandenbreemen.secretcamera.mvp.impl

import com.vandenbreemen.secretcamera.mvp.SFSMenuContract

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
class SFSMainMenuPresenterImpl(val mainMenuView: SFSMenuContract.SFSMainMenuView) : SFSMenuContract.SFSMainMenuPresenter {
    override fun takeNote() {
        mainMenuView.gotoTakeNote()
    }

    override fun takePicture() {
        mainMenuView.gotoTakePicture()
    }
}