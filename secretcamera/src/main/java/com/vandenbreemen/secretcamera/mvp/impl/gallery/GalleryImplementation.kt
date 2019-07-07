package com.vandenbreemen.secretcamera.mvp.impl.gallery

import android.graphics.Bitmap
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.log.SystemLog
import com.vandenbreemen.mobilesecurestorage.log.e
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Presenter
import com.vandenbreemen.secretcamera.mvp.gallery.AndroidImageInteractor
import com.vandenbreemen.secretcamera.mvp.gallery.GalleryPresenter
import com.vandenbreemen.secretcamera.mvp.gallery.GalleryView
import com.vandenbreemen.secretcamera.mvp.gallery.ImageFilesInteractor
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.computation

class GalleryModel(credentials: SFSCredentials) : Model(credentials) {

    companion object {
        val MAX_IMAGES = 3
    }

    private var androidImageInteractor = AndroidImageInteractor()
    lateinit var imageFilesInteractor: ImageFilesInteractor

    override fun onClose() {

    }

    override fun setup() {
        imageFilesInteractor = ImageFilesInteractor(sfs)
    }

    fun getImageThumbnails(): Single<List<Bitmap>> {
        return Single.create(SingleOnSubscribe<List<Bitmap>> {
            val list = imageFilesInteractor.listImageFiles()

            if (list.isEmpty()) {
                it.onSuccess(emptyList())
            }

            val numImages = if (list.size >= MAX_IMAGES) MAX_IMAGES else list.size

            val ret = mutableListOf<Bitmap>()
            var bitmap: Bitmap
            for (i in 0 until numImages) {
                try {
                    bitmap = androidImageInteractor.convertByteArrayToBitmapSynchronous(imageFilesInteractor.loadImageBytes(
                            list[i]
                    ))
                    ret.add(
                            androidImageInteractor.generateThumbnailSynchronous(bitmap, 150, 150)
                    )
                } catch (ex: Exception) {
                    SystemLog.get().e(AndroidImageInteractor::class.java.simpleName, "Failed to load or convert bitmap bytes", ex)
                }
            }

            it.onSuccess(ret)
        }).subscribeOn(computation())

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
        model.getImageThumbnails().observeOn(mainThread()).subscribe { thumbnails ->
            view.showExamples(thumbnails)
        }
    }

}