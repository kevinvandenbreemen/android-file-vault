package com.vandenbreemen.secretcamera.app

import android.app.Application
import com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions.FileTypeDisplayExtender
import com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions.FileTypeDisplayExtension
import com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions.FileTypeIcon
import com.vandenbreemen.mobilesecurestorage.file.api.FileType
import com.vandenbreemen.secretcamera.di.AppComponent
import com.vandenbreemen.secretcamera.di.DaggerAppComponent
import com.vandenbreemen.secretcamera.mvp.gallery.PictureFileIcons
import com.vandenbreemen.secretcamera.mvp.gallery.PicturesFileTypes

/**
 * Created by kevin on 24/03/18.
 */
class SecureCameraApp:Application() {

    companion object {
        lateinit var component: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        component = DaggerAppComponent
                .factory()
                .create(this)

        registerFileTypeIcons()
    }

    /**
     * Set up icons associated with file types specific to this app.
     */
    private fun registerFileTypeIcons() {
        FileTypeDisplayExtender.shared.register(object : FileTypeDisplayExtension {
            override fun iconFor(fileName: String, fileType: FileType): FileTypeIcon? {
                if (fileType == PicturesFileTypes.CAPTURED_IMAGE || fileType == PicturesFileTypes.IMPORTED_IMAGE) {
                    return PictureFileIcons.IMAGE
                }
                return null
            }

        })
    }
}