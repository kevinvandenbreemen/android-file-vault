package com.vandenbreemen.secretcamera

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.vandenbreemen.mobilesecurestorage.android.FileImportActivity
import com.vandenbreemen.mobilesecurestorage.android.FileImportDataProvider
import com.vandenbreemen.mobilesecurestorage.android.FileImportFutureIntent
import com.vandenbreemen.mobilesecurestorage.android.FileSelectActivity
import com.vandenbreemen.mobilesecurestorage.android.api.FileWorkflow
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.api.FileType
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.mvp.gallery.GalleryPresenter
import com.vandenbreemen.secretcamera.mvp.gallery.GalleryView
import com.vandenbreemen.secretcamera.mvp.gallery.PicturesFileTypes
import dagger.android.AndroidInjection
import javax.inject.Inject

class Gallery : AppCompatActivity(), GalleryView {


    @Inject
    lateinit var presenter: GalleryPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
    }

    fun onImportDir(view: View) {
        presenter.importDirectory()
    }

    override fun loadDirectoryImport(sfsCredentials: SFSCredentials) {
        val intent: Intent = Intent(this, FileSelectActivity::class.java)
        val workflow: FileWorkflow = FileWorkflow()
        workflow.targetActivity = FileImportActivity::class.java
        workflow.activityToStartAfterTargetActivityFinished = Gallery::class.java
        workflow.setTargetActivityFutureIntent(FileImportFutureIntent::class.java)
        FileImportFutureIntent().populateIntentWithDetailsAboutFutureActivity(intent, object : FileImportDataProvider {
            override fun getFileTypeToBeImported(): FileType {
                return PicturesFileTypes.IMPORTED_IMAGE
            }
        })
        intent.putExtra(FileWorkflow.PARM_WORKFLOW_NAME, workflow)
        intent.putExtra(FileSelectActivity.PARM_DIR_ONLY, true)
        intent.putExtra(FileSelectActivity.PARM_TITLE, resources.getText(com.vandenbreemen.mobilesecurestorage.R.string.loc_for_new_sfs))
        intent.putExtra(SFSCredentials.PARM_CREDENTIALS, sfsCredentials)
        startActivity(intent)
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
