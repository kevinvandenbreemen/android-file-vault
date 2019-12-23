package com.vandenbreemen.secretcamera.mvp.impl.gallery

import android.graphics.Bitmap
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.api.getSecureFileSystemInteractor
import com.vandenbreemen.mobilesecurestorage.log.SystemLog
import com.vandenbreemen.mobilesecurestorage.log.e
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Presenter
import com.vandenbreemen.secretcamera.mvp.gallery.*
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
    lateinit var galleryCommonInteractor: GalleryCommonInteractor

    override fun onClose() {

    }

    override fun setup() {
        imageFilesInteractor = ImageFilesInteractor(sfs)
        this.galleryCommonInteractor = GalleryCommonInteractor(getSecureFileSystemInteractor(sfs))
    }

    fun getImageThumbnails(): Single<List<Bitmap>> {
        return Single.create(SingleOnSubscribe<List<Bitmap>> {
            val list = imageFilesInteractor.listImageFiles()

            if (list.isEmpty()) {
                it.onSuccess(emptyList())
            }

            val numImages = if (list.size >= MAX_IMAGES) MAX_IMAGES else list.size
            val startImage = galleryCommonInteractor.getGallerySettings().currentFile
            var startIndex = 0
            startImage?.let { imgName ->
                startIndex = list.indexOf(imgName)
            }

            val ret = mutableListOf<Bitmap>()
            var bitmap: Bitmap
            for (i in startIndex until (startIndex + numImages)) {
                try {

                    val index = i % list.size

                    bitmap = androidImageInteractor.convertByteArrayToBitmapSynchronous(imageFilesInteractor.loadImageBytes(
                            list[index]
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
        addForDisposal(model.getImageThumbnails().observeOn(mainThread()).subscribe { thumbnails ->
            view.showExamples(thumbnails)
        })
    }

}