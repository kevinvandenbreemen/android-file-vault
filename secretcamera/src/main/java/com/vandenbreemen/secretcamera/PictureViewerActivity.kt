package com.vandenbreemen.secretcamera

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.mvp.gallery.PictureViewerPresenter
import com.vandenbreemen.secretcamera.mvp.gallery.PictureViewerView
import dagger.android.AndroidInjection
import javax.inject.Inject

class ThumbnailViewHolder(val view: ViewGroup) : RecyclerView.ViewHolder(view) {


}

class ThumbnailAdapter(private val fileNames: List<String>,
                       private val presenter: PictureViewerPresenter
) : RecyclerView.Adapter<ThumbnailViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailViewHolder {
        return ThumbnailViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_select_item, parent, false) as ViewGroup)
    }

    override fun getItemCount(): Int {
        return fileNames.size
    }

    override fun onBindViewHolder(holder: ThumbnailViewHolder, position: Int) {
        presenter.thumbnail(fileNames[position]).subscribe({ bitmap ->
            val imageView = holder.view.findViewById<ImageView>(R.id.preview)
            imageView.setImageBitmap(bitmap)

            val loadingSpinner = holder.view.findViewById<ProgressBar>(R.id.loading)
            loadingSpinner.visibility = GONE
        })
    }

}

class PictureViewerActivity : Activity(), PictureViewerView {

    @Inject
    lateinit var presenter: PictureViewerPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        //  See also https://developer.android.com/training/system-ui/status.html
        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.systemUiVisibility = uiOptions

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_picture_viewer)

        findViewById<RecyclerView>(R.id.pictureSelector).visibility = GONE

    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onPause() {
        super.onPause()
        findViewById<ViewGroup>(R.id.overlay).visibility = View.VISIBLE
        presenter.close()
        finish()
    }

    override fun onReadyToUse() {
        findViewById<ViewGroup>(R.id.overlay).visibility = View.GONE
        presenter.displayImage()
    }

    override fun showError(error: ApplicationError) {
        Toast.makeText(this, error.localizedMessage, LENGTH_SHORT).show()
    }

    override fun displayImage(imageToDisplay: Bitmap) {
        findViewById<SubsamplingScaleImageView>(R.id.currentImage).maxScale = 15F
        findViewById<SubsamplingScaleImageView>(R.id.currentImage).setImage(ImageSource.bitmap(imageToDisplay))
    }

    fun onForward(view: View) {
        presenter.nextImage()
    }

    fun onBack(view: View) {
        presenter.previousImage()
    }

    fun onShowSelector(view: View) {

    }

    override fun showImageSelector(files: List<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun end() {

    }
}
