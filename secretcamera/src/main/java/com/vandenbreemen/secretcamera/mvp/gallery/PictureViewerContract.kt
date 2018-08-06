package com.vandenbreemen.secretcamera.mvp.gallery

import android.graphics.Bitmap
import android.util.Log
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.api.getSecureFileSystemInteractor
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Presenter
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.PresenterContract
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.View
import com.vandenbreemen.mobilesecurestorage.security.crypto.listFiles
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.computation
import org.cache2k.Cache2kBuilder
import java.util.concurrent.Callable


class ImageFilesInteractor(private val sfs: SecureFileSystem) {

    companion object {
        private const val FILES_LIST_KEY = "__FILES_LIST"
    }

    private val sfsInteractor = getSecureFileSystemInteractor(sfs)
    private val cache = object : Cache2kBuilder<String, Any>() {

    }.build()

    fun listImageFiles(): List<String> {
        return cache.computeIfAbsent(FILES_LIST_KEY,
                Callable { sfs.listFiles(PicturesFileTypes.IMPORTED_IMAGE, PicturesFileTypes.CAPTURED_IMAGE).sorted() }) as List<String>
    }

    fun loadImageBytes(fileName: String): ByteArray {
        try {
            return sfs.loadBytesFromFile(fileName)
        } catch (exception: Exception) {
            Log.e(ImageFilesInteractor::class.java.simpleName, "Failed to load image bytes", exception)
            throw ApplicationError("Error loading $fileName")
        }
    }

    fun deleteImages(fileNames: List<String>) {
        this.sfsInteractor.delete(fileNames)
        this.cache.clear()  //  Force cache to reload since files were deleted
    }

    fun close() {
        cache.clear()
        cache.clearAndClose()
    }

}

interface PictureViewerView : View {
    fun displayImage(imageToDisplay: Bitmap)
    fun showImageSelector(files: List<String>)
    fun end()
    fun hideImageSelector()
    fun showLoadingSpinner()
    fun hideLoadingSpinner()
}

interface PictureViewRouter {
    fun showActions()
    fun hideActions()
    fun enableSelectMultiple()
    fun disableSelectMultiple()
    fun navigateBack(sfsCredentials: SFSCredentials)
}

interface PictureViewerPresenter : PresenterContract {
    fun displayCurrentImage()
    fun nextImage()
    fun previousImage()
    fun showSelector()
    fun thumbnail(fileName: String): Single<Bitmap>
    fun selectImageToDisplay(fileName: String)
    fun currentImageFileName(): Single<String>
    fun toggleSelectImages()
    fun selectImage(fileName: String)
    fun deleteSelected()
    fun selected(filename: String): Boolean
}

class PictureViewerPresenterImpl(val model: PictureViewerModel, val view: PictureViewerView, val router: PictureViewRouter) : Presenter<PictureViewerModel, PictureViewerView>(model, view), PictureViewerPresenter {
    override fun toggleSelectImages() {
        if (model.isImageMultiselectOn()) {
            model.enableImageMultiSelect(false)
            router.disableSelectMultiple()
            router.hideActions()
        } else {
            model.enableImageMultiSelect(true)
            router.enableSelectMultiple()
            router.showActions()
        }
    }

    override fun selectImageToDisplay(fileName: String) {
        view.showLoadingSpinner()
        model.loadImageForDisplay(fileName).subscribe({ image ->
            showImageOnView(image)
            view.hideImageSelector()
        })
    }

    private fun showImageOnView(image: Bitmap) {
        view.displayImage(image)
        view.hideLoadingSpinner()
    }


    override fun displayCurrentImage() {
        view.showLoadingSpinner()
        model.currentFile().flatMap { imageFile ->
            model.loadImageForDisplay(imageFile)
        }.subscribe({ bitmap -> showImageOnView(bitmap) },
                { error -> view.showError(ApplicationError(error)) }
        )
    }

    override fun nextImage() {
        view.showLoadingSpinner()
        model.nextFile().flatMap { imageFile ->
            model.loadImage(imageFile)
        }
                .observeOn(mainThread())
                .subscribeOn(computation())
                .subscribe({ bitmap -> showImageOnView(bitmap) },
                { error -> view.showError(ApplicationError(error)) }
        )
    }

    override fun previousImage() {
        view.showLoadingSpinner()
        model.prevFile().flatMap { imageFile ->
            model.loadImage(imageFile)
        }.subscribe({ bitmap -> showImageOnView(bitmap) },
                { error -> view.showError(ApplicationError(error)) }
        )
    }

    override fun thumbnail(fileName: String): Single<Bitmap> {
        return model.loadImage(fileName).observeOn(mainThread())
                .flatMap { bitmap ->
                    model.getThumbnail(bitmap)
                }.observeOn(mainThread())
                .subscribeOn(computation())
    }

    override fun showSelector() {
        model.listImages().subscribe({ imageFiles -> view.showImageSelector(imageFiles) })
    }

    override fun currentImageFileName(): Single<String> {
        return model.currentFile()
    }

    override fun setupView() {

    }

    override fun selectImage(fileName: String) {
        if (model.isSelected(fileName)) {
            model.deselectImage(fileName)
        } else {
            model.selectImage(fileName)
        }
    }

    override fun deleteSelected() {
        if (model.hasSelectedImages()) {
            router.hideActions()
            view.hideImageSelector()
            view.showLoadingSpinner()
            model.deleteSelected().observeOn(mainThread()).subscribe {
                model.hasMoreImages().subscribe { hasMore ->
                    if (!hasMore) {
                        router.navigateBack(model.copyCredentials())
                    } else {
                        view.hideLoadingSpinner()
                        displayCurrentImage()
                    }
                }
            }
        } else {
            view.showError(ApplicationError("No Images Selected For Delete"))
        }
    }

    override fun selected(filename: String): Boolean {
        return model.isSelected(filename)
    }
}