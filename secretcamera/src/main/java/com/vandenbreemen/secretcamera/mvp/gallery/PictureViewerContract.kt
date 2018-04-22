package com.vandenbreemen.secretcamera.mvp.gallery

import android.graphics.Bitmap
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Presenter
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.PresenterContract
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.View
import com.vandenbreemen.mobilesecurestorage.security.crypto.listFiles
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem


class ImageFilesInteractor(private val sfs: SecureFileSystem) {
    fun listImageFiles(): List<String> {
        return sfs.listFiles(PicturesFileTypes.IMPORTED_IMAGE).sorted()
    }

    fun loadImageBytes(fileName: String): ByteArray {
        return sfs.loadBytesFromFile(fileName)
    }

}

interface PictureViewerView : View {
    fun displayImage(imageToDisplay: Bitmap)

}

interface PictureViewerPresenter : PresenterContract {
    fun displayImage()

}

class PictureViewerPresenterImpl(val model: PictureViewerModel, val view: PictureViewerView) : Presenter<PictureViewerModel, PictureViewerView>(model, view), PictureViewerPresenter {
    override fun displayImage() {

        model.currentFile().flatMap { imageFile ->
            model.loadImage(imageFile)
        }.subscribe({ bitmap -> view.displayImage(bitmap) },
                { error -> view.showError(ApplicationError(error)) }
        )
    }

    override fun setupView() {

    }

}