package com.vandenbreemen.secretcamera

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.View
import com.wonderkiln.camerakit.*

class TakePictureActivity : Activity() {

    lateinit var cameraView: CameraView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_picture)
        this.cameraView = findViewById<CameraView>(R.id.camera)

        this.cameraView.addCameraKitListener(
                object : CameraKitEventListener {
                    override fun onVideo(p0: CameraKitVideo?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onEvent(p0: CameraKitEvent?) {
                        Log.d("KevinDebug", "Event ${p0.toString()}")
                    }

                    override fun onImage(image: CameraKitImage?) {
                        Log.d("KevinTest", "Captured ${image!!.jpeg}")
                    }

                    override fun onError(p0: CameraKitError?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                }
        )

        findViewById<FloatingActionButton>(R.id.takePicture).setOnClickListener(View.OnClickListener { v -> cameraView.captureImage() })
    }

    override fun onResume() {
        super.onResume()
        this.cameraView.start()
    }

    override fun onPause() {
        this.cameraView.stop()
        super.onPause()
    }
}
