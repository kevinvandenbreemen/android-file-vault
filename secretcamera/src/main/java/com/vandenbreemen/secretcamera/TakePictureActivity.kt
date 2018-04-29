package com.vandenbreemen.secretcamera

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.View
import android.widget.Toast
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.mvp.takepicture.TakePicturePresenter
import com.vandenbreemen.secretcamera.mvp.takepicture.TakePictureView
import com.wonderkiln.camerakit.*
import dagger.android.AndroidInjection
import javax.inject.Inject

class TakePictureActivity : Activity(), TakePictureView {

    companion object {
        const val TAG = "TakePictureActivity"
    }

    lateinit var cameraView: CameraView

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
    }

    override fun onPause() {
        this.cameraView.stop()
        super.onPause()
    }

    override fun onReadyToUse() {
        this.cameraView.start()
        this.cameraView = findViewById<CameraView>(R.id.camera)
        this.cameraView.addCameraKitListener(
                object : CameraKitEventListener {
                    override fun onVideo(p0: CameraKitVideo?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onEvent(p0: CameraKitEvent?) {
                        Log.d("KevinDebug", "Event ${p0.toString()}")
                    }

                    override fun onImage(image: CameraKitImage) {
                        //Log.d("KevinTest", "Captured ${image!!.jpeg}")
                        presenter.capture(image.jpeg)
                    }

                    override fun onError(p0: CameraKitError) {
                        Log.e(TAG, "Camera Error", p0.exception)
                        showError(ApplicationError(p0.toString()))
                    }

                }
        )

        findViewById<FloatingActionButton>(R.id.takePicture).setOnClickListener(View.OnClickListener { v -> cameraView.captureImage() })
    }

    override fun showError(error: ApplicationError) {
        Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
    }
}
