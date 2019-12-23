package com.vandenbreemen.secretcamera.mvp.gallery

import android.graphics.Bitmap
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.api.FileInfo
import com.vandenbreemen.mobilesecurestorage.file.api.SecureFileSystemInteractor
import com.vandenbreemen.mobilesecurestorage.file.api.getSecureFileSystemInteractor
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import com.vandenbreemen.secretcamera.api.GallerySettings
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.computation

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
class PictureViewerModel(credentials: SFSCredentials) : Model(credentials) {

    companion object {
        const val SETTINGS = ".gallerySettings"
    }

    lateinit var imageFilesInteractor: ImageFilesInteractor
    lateinit var androidImageInteractor: AndroidImageInteractor
    lateinit var secureFileSystemInteractor: SecureFileSystemInteractor
    lateinit var galleryCommonInteractor: GalleryCommonInteractor

    private var selectedFiles: MutableList<String>? = null

    override fun onClose() {
        imageFilesInteractor.close()
    }

    override fun setup() {
        this.imageFilesInteractor = ImageFilesInteractor(sfs)
        this.androidImageInteractor = AndroidImageInteractor()
        this.secureFileSystemInteractor = getSecureFileSystemInteractor(sfs)
        this.galleryCommonInteractor = GalleryCommonInteractor(this.secureFileSystemInteractor)
    }

    fun selectImage(fileName: String) {
        this.selectedFiles!!.add(fileName)
    }

    fun deselectImage(fileName: String) {
        this.selectedFiles!!.remove(fileName)
    }

    fun enableImageMultiSelect(enabled: Boolean) {
        if (enabled) {
            this.selectedFiles = mutableListOf()
        } else {
            this.selectedFiles = null
        }
    }

    fun isImageMultiselectOn(): Boolean {
        return this.selectedFiles != null
    }

    fun loadImageForDisplay(fileName: String): Single<Bitmap> {
        return Single.create(SingleOnSubscribe<ByteArray> {
            val gallerySettings = getGallerySettings()
            gallerySettings.currentFile = fileName
            saveGallerySettings(gallerySettings)
            it.onSuccess(imageFilesInteractor.loadImageBytes(gallerySettings.currentFile!!))
        })
                .subscribeOn(Schedulers.computation())
                .flatMap { imageBytes ->
                    androidImageInteractor.convertByteArrayToBitmap(imageBytes).observeOn(AndroidSchedulers.mainThread())
                }
    }

    fun loadImage(filename: String): Single<Bitmap> {
        return Single.create(SingleOnSubscribe<ByteArray> {
            it.onSuccess(imageFilesInteractor.loadImageBytes(filename))
        })
                .subscribeOn(Schedulers.computation())
                .flatMap { imageBytes ->
                    androidImageInteractor.convertByteArrayToBitmap(imageBytes).observeOn(AndroidSchedulers.mainThread())
                }
    }

    fun listImages(): Single<List<String>> {
        return Single.create(SingleOnSubscribe<List<String>> {
            it.onSuccess(this.imageFilesInteractor.listImageFiles())
        }).subscribeOn(Schedulers.computation()).observeOn(mainThread())
    }

    private fun getGallerySettings(): GallerySettings {
        return galleryCommonInteractor.getGallerySettings()
    }

    fun currentFile(): Single<String> {
        return Single.create(SingleOnSubscribe<String> { emitter ->
            val gallerySettings = getGallerySettings()
            gallerySettings.currentFile?.let {
                emitter.onSuccess(it)
                return@SingleOnSubscribe
            }

            val listOfFiles = this.imageFilesInteractor.listImageFiles()
            if (listOfFiles.isEmpty()) {
                emitter.onError(ApplicationError("No images available"))
                return@SingleOnSubscribe
            }

            gallerySettings.currentFile = listOfFiles[0]
            saveGallerySettings(gallerySettings)
            emitter.onSuccess(gallerySettings.currentFile)
        }).subscribeOn(computation()).observeOn(mainThread())
    }

    fun getFileInfo(fileName: String): FileInfo {
        return secureFileSystemInteractor.info(fileName)
    }

    fun nextFile(): Single<String> {
        return Single.create(SingleOnSubscribe<String> {

            val gallerySettings = getGallerySettings()
            val nextFile = getNextFile(gallerySettings)
            gallerySettings.currentFile = nextFile
            it.onSuccess(gallerySettings.currentFile)
            saveGallerySettings(gallerySettings)

        }).subscribeOn(computation()).observeOn(mainThread())
    }

    private fun getNextFile(gallerySettings: GallerySettings): String {
        val listOfFiles = this.imageFilesInteractor.listImageFiles()
        val currentIndex = listOfFiles.indexOf(gallerySettings.currentFile)

        var nextIndex = currentIndex + 1
        if (currentIndex >= listOfFiles.size - 1) {
            nextIndex = 0
        }

        val nextFile = listOfFiles[nextIndex]
        return nextFile
    }

    fun prevFile(): Single<String> {
        return Single.create(SingleOnSubscribe<String> {
            val gallerySettings = getGallerySettings()
            val prevFile = getPreviousFile(gallerySettings)
            gallerySettings.currentFile = prevFile
            it.onSuccess(gallerySettings.currentFile)
            saveGallerySettings(gallerySettings)
        }).subscribeOn(computation()).observeOn(mainThread())
    }

    private fun saveGallerySettings(gallerySettings: GallerySettings) {
        galleryCommonInteractor.saveGallerySettings(gallerySettings)
    }

    private fun getPreviousFile(gallerySettings: GallerySettings): String {
        val listOfFiles = this.imageFilesInteractor.listImageFiles()
        val currentIndex = listOfFiles.indexOf(gallerySettings.currentFile)

        var nextIndex = currentIndex - 1
        if (nextIndex < 0) {
            nextIndex = listOfFiles.size - 1
        }

        val prevFile = listOfFiles[nextIndex]
        return prevFile
    }

    fun getThumbnail(imageBitmap: Bitmap): Single<Bitmap> {
        return androidImageInteractor.generateThumbnail(imageBitmap)
    }

    fun deleteSelected(): Completable {
        return Completable.create {
            val gallerySettings = getGallerySettings()
            this.imageFilesInteractor.deleteImages(this.selectedFiles!!.toList())
            if (gallerySettings.currentFile != null && selectedFiles!!.contains(gallerySettings.currentFile!!)) {
                if (imageFilesInteractor.listImageFiles().isNotEmpty()) {
                    gallerySettings.currentFile = getNextFile(gallerySettings)
                } else {
                    gallerySettings.currentFile = null
                }
                saveGallerySettings(gallerySettings)
            }
            it.onComplete()
        }.subscribeOn(computation())
    }

    fun hasSelectedImages(): Boolean {
        return if (this.selectedFiles == null) false else {
            selectedFiles!!.size > 0
        }
    }

    fun isSelected(fileName: String): Boolean {
        return if (this.selectedFiles != null) this.selectedFiles!!.contains(fileName) else false
    }

    fun hasMoreImages(): Single<Boolean> {
        return listImages().flatMap<Boolean> { files ->
            Single.just(files.isNotEmpty())
        }.subscribeOn(computation())
    }

    fun deleteAllImages(): Completable {
        return Completable.create {
            imageFilesInteractor.deleteImages(imageFilesInteractor.listImageFiles())
            val settings = getGallerySettings()
            settings.currentFile = null
            saveGallerySettings(settings)
            it.onComplete()
        }.subscribeOn(computation())
    }

}