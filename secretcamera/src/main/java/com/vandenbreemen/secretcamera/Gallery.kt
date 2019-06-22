package com.vandenbreemen.secretcamera

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import com.vandenbreemen.mobilesecurestorage.android.FileImportActivity
import com.vandenbreemen.mobilesecurestorage.android.FileImportDataProvider
import com.vandenbreemen.mobilesecurestorage.android.FileImportFutureIntent
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.api.FileType
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.mvp.gallery.GalleryPresenter
import com.vandenbreemen.secretcamera.mvp.gallery.GalleryView
import com.vandenbreemen.secretcamera.mvp.gallery.PicturesFileTypes
import dagger.android.AndroidInjection
import javax.inject.Inject

class Gallery : AppCompatActivity(), GalleryView {

    companion object {
        const val ACTION_IMPORT = 34
    }

    @Inject
    lateinit var presenter: GalleryPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
    }

    override fun onPause() {
        super.onPause()
        findViewById(R.id.overlay).visibility = View.VISIBLE
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

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onReadyToUse() {

    }

    override fun showError(error: ApplicationError) {

    }
}
