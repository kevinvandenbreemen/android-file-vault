package com.vandenbreemen.secretcamera.mvp.impl.gallery

import android.graphics.Bitmap
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.api.getSecureFileSystemInteractor
import com.vandenbreemen.mobilesecurestorage.log.SystemLog
import com.vandenbreemen.mobilesecurestorage.log.e
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Presenter
import com.vandenbreemen.secretcamera.mvp.gallery.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    fun getImageThumbnails(): List<Bitmap> {
        val list = imageFilesInteractor.listImageFiles()

        if (list.isEmpty()) {
            return emptyList()
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

                bitmap = androidImageInteractor.convertByteArrayToBitmap(imageFilesInteractor.loadImageBytes(
                        list[index]
                ))
                ret.add(
                        androidImageInteractor.generateThumbnailSynchronous(bitmap, 150, 150)
                )
            } catch (ex: Exception) {
                SystemLog.get().e(AndroidImageInteractor::class.java.simpleName, "Failed to load or convert bitmap bytes", ex)
            }
        }

        return ret

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
        CoroutineScope(Dispatchers.Default).launch {
            val thumbnails = model.getImageThumbnails()
            view.showExamples(thumbnails)
        }

    }

}