package com.vandenbreemen.secretcamera.mvp.takepicture

import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Presenter
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.PresenterContract
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.View

interface TakePicturePresenter : PresenterContract {
    fun capture(pictureBytes: ByteArray)


}

interface TakePictureView : View {

}

class TakePicturePresenterImpl(val model: TakePictureModel, val view: TakePictureView) : Presenter<TakePictureModel, TakePictureView>(model, view), TakePicturePresenter {
    override fun capture(pictureBytes: ByteArray) {
        model.storePicture(pictureBytes).subscribe()
    }

    override fun setupView() {

    }

}