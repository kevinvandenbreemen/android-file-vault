package com.vandenbreemen.secretcamera.mvp.impl.gallery

import android.graphics.Bitmap
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Presenter
import com.vandenbreemen.secretcamera.mvp.gallery.AndroidImageInteractor
import com.vandenbreemen.secretcamera.mvp.gallery.GalleryPresenter
import com.vandenbreemen.secretcamera.mvp.gallery.GalleryView
import com.vandenbreemen.secretcamera.mvp.gallery.ImageFilesInteractor
import io.reactivex.Single

class GalleryModel(credentials: SFSCredentials) : Model(credentials) {

    private var androidImageInteractor = AndroidImageInteractor()
    lateinit var imageFilesInteractor: ImageFilesInteractor

    override fun onClose() {

    }

    override fun setup() {
        imageFilesInteractor = ImageFilesInteractor(sfs)
    }

    fun getImageThumbnails(): Single<List<Bitmap>> {
        return Single.create {
            val list = imageFilesInteractor.listImageFiles()
            val ret = mutableListOf<Bitmap>()
            var bitmap: Bitmap
            for (i in 0 until 3) {
                bitmap = androidImageInteractor.convertByteArrayToBitmapSynchronous(imageFilesInteractor.loadImageBytes(
                        list[i]
                ))
                ret.add(
                        androidImageInteractor.generateThumbnailSynchronous(bitmap, 150, 150)
                )
            }

            it.onSuccess(ret)
        }

    }

}

class GalleryPresenterImpl(val model: GalleryModel, val view: GalleryView) : Presenter<GalleryModel, GalleryView>(model, view), GalleryPresenter {
    override fun viewPictures() {
        view.loadPictureViewer(model.copyCredentials())
    }

    override fun importDirectory() {
        view.loadDirectoryImport(model.copyCredentials())
    }

    override fun setupView() {

    }

}