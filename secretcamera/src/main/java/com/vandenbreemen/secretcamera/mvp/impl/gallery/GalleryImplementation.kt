package com.vandenbreemen.secretcamera.mvp.impl.gallery

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Presenter
import com.vandenbreemen.secretcamera.mvp.gallery.GalleryPresenter
import com.vandenbreemen.secretcamera.mvp.gallery.GalleryView

class GalleryModel(credentials: SFSCredentials) : Model(credentials) {
    override fun onClose() {

    }

    override fun setup() {

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