package com.vandenbreemen.secretcamera.mvp.gallery

import android.graphics.Bitmap
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
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

}

interface PictureViewerPresenter : PresenterContract {

}