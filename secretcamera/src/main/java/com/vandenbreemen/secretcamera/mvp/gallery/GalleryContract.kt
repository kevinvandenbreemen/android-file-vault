package com.vandenbreemen.secretcamera.mvp.gallery

import android.graphics.Bitmap
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.PresenterContract
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.View

interface GalleryView : View {
    fun loadDirectoryImport(sfsCredentials: SFSCredentials)
    fun loadPictureViewer(sfsCredentials: SFSCredentials)
    fun showExamples(thumbnails: List<Bitmap>)
}

interface GalleryPresenter : PresenterContract {
    fun importDirectory()
    fun viewPictures()

}