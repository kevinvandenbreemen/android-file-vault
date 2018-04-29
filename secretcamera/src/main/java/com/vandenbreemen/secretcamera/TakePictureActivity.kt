package com.vandenbreemen.secretcamera

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.View
import android.widget.Toast
import com.camerakit.CameraKitView
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.mvp.takepicture.TakePicturePresenter
import com.vandenbreemen.secretcamera.mvp.takepicture.TakePictureView
import dagger.android.AndroidInjection
import javax.inject.Inject

class TakePictureActivity : Activity(), TakePictureView {

    companion object {
        const val TAG = "TakePictureActivity"
    }

    lateinit var cameraView: CameraKitView

    @Inject
    lateinit var presenter: TakePicturePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_picture)
        this.cameraView = findViewById(R.id.camera)
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
        cameraView.onResume()
    }

    override fun onPause() {
        cameraView.onPause()
        super.onPause()
    }

    override fun onReadyToUse() {


        findViewById<FloatingActionButton>(R.id.takePicture).setOnClickListener(View.OnClickListener { v ->
            cameraView.captureImage(CameraKitView.ImageCallback { cameraKitView, bytes -> presenter.capture(bytes) })
        })
    }

    override fun showError(error: ApplicationError) {
        Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
    }
}
