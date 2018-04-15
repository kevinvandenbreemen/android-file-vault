package com.vandenbreemen.secretcamera.mvp.gallery

import android.graphics.Bitmap
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Presenter
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.PresenterContract
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.View
import com.vandenbreemen.mobilesecurestorage.security.crypto.listFiles
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.computation


class PictureViewerModel(credentials: SFSCredentials) : Model(credentials) {

    lateinit var imageFilesInteractor: ImageFilesInteractor
    lateinit var androidImageInteractor: AndroidImageInteractor

    override fun onClose() {

    }

    override fun setup() {
        this.imageFilesInteractor = ImageFilesInteractor(sfs)
        this.androidImageInteractor = AndroidImageInteractor()
    }

    fun loadImage(fileName: String): Single<Bitmap> {
        return Single.create(SingleOnSubscribe<ByteArray> {
            it.onSuccess(imageFilesInteractor.loadImageBytes(fileName))
        })
                .subscribeOn(computation())
                .flatMap { imageBytes ->
                    androidImageInteractor.convertByteArrayToBitmap(imageBytes).observeOn(mainThread())
                }
    }

    fun listImages(): Single<List<String>> {
        return Single.create(SingleOnSubscribe<List<String>> {
            it.onSuccess(this.imageFilesInteractor.listImageFiles())
        }).subscribeOn(computation()).observeOn(mainThread())
    }

}

class ImageFilesInteractor(private val sfs: SecureFileSystem) {
    fun listImageFiles(): List<String> {
        return sfs.listFiles(PicturesFileTypes.IMPORTED_IMAGE)
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
        model.listImages().flatMap { imageFiles ->
            model.loadImage(imageFiles[0])
        }.subscribe({ bitmap -> view.displayImage(bitmap) })
    }

    override fun setupView() {

    }

}