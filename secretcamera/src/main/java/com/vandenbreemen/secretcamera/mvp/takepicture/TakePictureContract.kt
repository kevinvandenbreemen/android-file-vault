package com.vandenbreemen.secretcamera.mvp.takepicture

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Presenter
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.PresenterContract
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.View

interface TakePicturePresenter : PresenterContract {
    fun capture(pictureBytes: ByteArray)
    fun back()


}

interface TakePictureView : View {
    fun returnToMain(fakeCredentials: SFSCredentials)

}

class TakePicturePresenterImpl(val model: TakePictureModel, val view: TakePictureView) : Presenter<TakePictureModel, TakePictureView>(model, view), TakePicturePresenter {
    override fun back() {
        view.returnToMain(model.copyCredentials())
    }

    override fun capture(pictureBytes: ByteArray) {
        addForDisposal(model.storePicture(pictureBytes).subscribe())
    }

    override fun setupView() {

    }

}