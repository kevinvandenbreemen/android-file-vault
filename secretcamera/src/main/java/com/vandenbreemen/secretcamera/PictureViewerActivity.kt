package com.vandenbreemen.secretcamera

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.mvp.gallery.PictureViewerPresenter
import com.vandenbreemen.secretcamera.mvp.gallery.PictureViewerView
import dagger.android.AndroidInjection
import javax.inject.Inject

class PictureViewerActivity : Activity(), PictureViewerView {

    @Inject
    lateinit var presenter: PictureViewerPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_viewer)
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onReadyToUse() {
        presenter.displayImage()
    }

    override fun showError(error: ApplicationError) {
        Toast.makeText(this, error.localizedMessage, LENGTH_SHORT).show()
    }

    override fun displayImage(imageToDisplay: Bitmap) {
        findViewById<ImageView>(R.id.currentImage).setImageBitmap(imageToDisplay)
    }
}
