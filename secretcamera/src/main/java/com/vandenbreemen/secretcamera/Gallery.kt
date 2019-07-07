package com.vandenbreemen.secretcamera

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.vandenbreemen.mobilesecurestorage.android.FileImportActivity
import com.vandenbreemen.mobilesecurestorage.android.FileImportDataProvider
import com.vandenbreemen.mobilesecurestorage.android.FileImportFutureIntent
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.api.FileType
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.di.injectGallery
import com.vandenbreemen.secretcamera.mvp.gallery.GalleryPresenter
import com.vandenbreemen.secretcamera.mvp.gallery.GalleryView
import com.vandenbreemen.secretcamera.mvp.gallery.PicturesFileTypes
import javax.inject.Inject

class Gallery : AppCompatActivity(), GalleryView {

    companion object {
        const val ACTION_IMPORT = 34
    }

    @Inject
    lateinit var presenter: GalleryPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        injectGallery(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
    }

    override fun onPause() {
        super.onPause()
        (findViewById(R.id.overlay) as View).visibility = View.VISIBLE
        if (!presenter.isClosed()) {
            presenter.close()
        }
    }

    fun onImportDir(view: View) {
        presenter.importDirectory()
    }

    fun onViewPictures(view: View) {
        presenter.viewPictures()
    }

    override fun loadPictureViewer(sfsCredentials: SFSCredentials) {
        val intent = Intent(this, PictureViewerActivity::class.java)
        intent.putExtra(SFSCredentials.PARM_CREDENTIALS, sfsCredentials)
        startActivity(intent)
        finish()
    }

    override fun loadDirectoryImport(sfsCredentials: SFSCredentials) {
        val intent: Intent = Intent(this, FileImportActivity::class.java)
        intent.putExtra(SFSCredentials.PARM_CREDENTIALS, sfsCredentials)

        FileImportFutureIntent().populateIntentWithDetailsAboutFutureActivity(intent, object : FileImportDataProvider {
            override fun getFileTypeToBeImported(): FileType {
                return PicturesFileTypes.IMPORTED_IMAGE
            }
        })

        startActivityForResult(intent, ACTION_IMPORT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ACTION_IMPORT) {
            if (resultCode == Activity.RESULT_OK) {
                val credentials = data!!.getParcelableExtra<SFSCredentials>(SFSCredentials.PARM_CREDENTIALS)
                val startIntent = Intent(this, Gallery::class.java)
                startIntent.putExtra(SFSCredentials.PARM_CREDENTIALS, credentials)
                startActivity(startIntent)
                finish()
            } else {
                finish()
            }
        }
    }

    override fun showExamples(thumbnails: List<Bitmap>) {
        if(thumbnails.isNotEmpty()) {
            for(i in 0 until thumbnails.size) {
                val identifier = resources.getIdentifier("preview_img_${i+1}", "id", packageName)
                val imageView = findViewById<ImageView>(identifier)
                imageView.setImageBitmap(thumbnails[i])
                imageView.visibility = VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onReadyToUse() {

    }

    override fun showError(error: ApplicationError) {

    }
}
