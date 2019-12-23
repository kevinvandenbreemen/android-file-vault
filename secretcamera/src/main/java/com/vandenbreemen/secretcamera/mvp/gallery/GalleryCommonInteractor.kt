package com.vandenbreemen.secretcamera.mvp.gallery

import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes
import com.vandenbreemen.mobilesecurestorage.file.api.SecureFileSystemInteractor
import com.vandenbreemen.secretcamera.api.GallerySettings

/**
 *
 * @author kevin
 */
class GalleryCommonInteractor(private val secureFileSystemInteractor: SecureFileSystemInteractor) {

    fun getGallerySettings(): GallerySettings {
        secureFileSystemInteractor.load(PictureViewerModel.SETTINGS, FileTypes.DATA)?.let { loaded ->
            val gallerySettings = loaded as GallerySettings
            return gallerySettings
        }
        val gallerySettings = GallerySettings(null)
        saveGallerySettings(gallerySettings)
        return gallerySettings
    }

    fun saveGallerySettings(gallerySettings: GallerySettings) {
        secureFileSystemInteractor.save(gallerySettings, PictureViewerModel.SETTINGS, FileTypes.DATA)
    }

}