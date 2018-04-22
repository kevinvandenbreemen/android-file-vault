package com.vandenbreemen.secretcamera.mvp.gallery

import android.graphics.Bitmap
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes
import com.vandenbreemen.mobilesecurestorage.file.api.SecureFileSystemInteractor
import com.vandenbreemen.mobilesecurestorage.file.api.getSecureFileSystemInteractor
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import com.vandenbreemen.secretcamera.api.GallerySettings
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

    override fun onClose() {

    }

    override fun setup() {
        this.imageFilesInteractor = ImageFilesInteractor(sfs)
        this.androidImageInteractor = AndroidImageInteractor()
        this.secureFileSystemInteractor = getSecureFileSystemInteractor(sfs)
    }

    fun loadImage(fileName: String): Single<Bitmap> {
        return Single.create(SingleOnSubscribe<ByteArray> {
            it.onSuccess(imageFilesInteractor.loadImageBytes(fileName))
        })
                .subscribeOn(Schedulers.computation())
                .flatMap { imageBytes ->
                    androidImageInteractor.convertByteArrayToBitmap(imageBytes).observeOn(AndroidSchedulers.mainThread())
                }
    }

    fun listImages(): Single<List<String>> {
        return Single.create(SingleOnSubscribe<List<String>> {
            it.onSuccess(this.imageFilesInteractor.listImageFiles())
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
    }

    private fun getGallerySettings(): GallerySettings {
        secureFileSystemInteractor.load(SETTINGS, FileTypes.DATA)?.let { loaded ->
            val gallerySettings = loaded as GallerySettings
            return gallerySettings
        }
        val gallerySettings = GallerySettings(null)
        secureFileSystemInteractor.save(gallerySettings, SETTINGS, FileTypes.DATA)
        return gallerySettings
    }

    fun currentFile(): Single<String> {
        return Single.create(SingleOnSubscribe<String> { emitter ->
            val gallerySettings = getGallerySettings()
            gallerySettings.currentFile?.let {
                emitter.onSuccess(it)
            }

            val listOfFiles = this.imageFilesInteractor.listImageFiles()
            if (listOfFiles.isEmpty()) {
                emitter.onError(ApplicationError("No images available"))
                return@SingleOnSubscribe
            }

            gallerySettings.currentFile = listOfFiles[0]
            secureFileSystemInteractor.save(gallerySettings, SETTINGS, FileTypes.DATA)
            emitter.onSuccess(gallerySettings.currentFile)
        }).subscribeOn(computation()).observeOn(mainThread())
    }

    fun nextFile(): Single<String> {
        return Single.create(SingleOnSubscribe<String> {

            val gallerySettings = getGallerySettings()
            val listOfFiles = this.imageFilesInteractor.listImageFiles()
            val currentIndex = listOfFiles.indexOf(gallerySettings.currentFile)
            val nextFile = listOfFiles[currentIndex + 1]
            gallerySettings.currentFile = nextFile
            secureFileSystemInteractor.save(gallerySettings, SETTINGS, FileTypes.DATA)
            it.onSuccess(gallerySettings.currentFile)

        }).subscribeOn(computation()).observeOn(mainThread())
    }

    fun prevFile(): Single<String> {
        return Single.create(SingleOnSubscribe<String> {
            val gallerySettings = getGallerySettings()
            val listOfFiles = this.imageFilesInteractor.listImageFiles()
            val currentIndex = listOfFiles.indexOf(gallerySettings.currentFile)
            val prevFile = listOfFiles[currentIndex - 1]
            gallerySettings.currentFile = prevFile
            secureFileSystemInteractor.save(gallerySettings, SETTINGS, FileTypes.DATA)
            it.onSuccess(gallerySettings.currentFile)
        }).subscribeOn(computation()).observeOn(mainThread())
    }

}