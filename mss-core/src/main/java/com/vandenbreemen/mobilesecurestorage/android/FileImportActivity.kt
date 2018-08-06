package com.vandenbreemen.mobilesecurestorage.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import com.vandenbreemen.mobilesecurestorage.R
import com.vandenbreemen.mobilesecurestorage.android.api.FileWorkflow
import com.vandenbreemen.mobilesecurestorage.android.api.FileWorkflow.PARM_WORKFLOW_NAME
import com.vandenbreemen.mobilesecurestorage.android.api.FutureIntent
import com.vandenbreemen.mobilesecurestorage.android.mvp.importfiles.FileImportModel
import com.vandenbreemen.mobilesecurestorage.android.mvp.importfiles.FileImportPresenter
import com.vandenbreemen.mobilesecurestorage.android.mvp.importfiles.FileImportPresenterImpl
import com.vandenbreemen.mobilesecurestorage.android.mvp.importfiles.FileImportView
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.api.FileType
import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import java.io.File

interface FileImportDataProvider {
    fun getFileTypeToBeImported(): FileType
}

class FileImportFutureIntent : FutureIntent<FileImportDataProvider> {
    override fun populateIntentWithDetailsAboutFutureActivity(intent: Intent, provider: FileImportDataProvider) {
        val byteArray = if (provider.getFileTypeToBeImported().secondByte != null)
            byteArrayOf(provider.getFileTypeToBeImported().firstByte, provider.getFileTypeToBeImported().secondByte!!)
        else
            byteArrayOf(provider.getFileTypeToBeImported().firstByte)

        intent.putExtra(FileImportActivity.PARM_FILE_TYPE_BYTES, byteArray)
    }

    override fun populateIntentToStartFutureActivity(intentToStartFutureActivity: Intent, intentForCurrentActivity: Intent) {
        intentToStartFutureActivity.putExtra(
                FileImportActivity.PARM_FILE_TYPE_BYTES,
                intentForCurrentActivity.getByteArrayExtra(FileImportActivity.PARM_FILE_TYPE_BYTES))
    }

}

class FileImportActivity : Activity(), FileImportView {

    companion object {
        const val PARM_FILE_TYPE_BYTES = "__fileTypeBytes"
        const val SELECT_DIR = 1
    }

    override fun showTotalFiles(totalFiles: Int) {
        findViewById<ProgressBar>(R.id.progressBar).max = totalFiles
    }

    override fun updateProgress(numberOfFilesImported: Int) {
        findViewById<ProgressBar>(R.id.progressBar).progress = numberOfFilesImported
    }

    lateinit var directoryToImport: File

    lateinit var fileImportPresenter: FileImportPresenter

    override fun done(sfsCredentials: SFSCredentials) {

        val intentDone = Intent()
        intentDone.putExtra(SFSCredentials.PARM_CREDENTIALS, sfsCredentials)

        fileImportPresenter.close()
        setResult(RESULT_OK, intentDone)

        finish()
    }

    override fun onReadyToUse() {
        var fileType: FileType? = null
        intent.getByteArrayExtra(PARM_FILE_TYPE_BYTES)?.let { byteArray ->
            val bytes = Array<Byte?>(byteArray.size, { it -> byteArray[it] })
            fileType = FileTypes.getFileType(bytes)
        }
        fileImportPresenter.import(directoryToImport, fileType)
    }

    override fun showError(error: ApplicationError) {
        Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_import)

        val selectDirIntent = Intent(this, FileSelectActivity::class.java)
        selectDirIntent.putExtra(FileSelectActivity.PARM_DIR_ONLY, true)
        selectDirIntent.putExtra(FileSelectActivity.PARM_TITLE, resources.getText(com.vandenbreemen.mobilesecurestorage.R.string.loc_for_new_sfs))
        selectDirIntent.putExtra(SFSCredentials.PARM_CREDENTIALS, intent.getParcelableExtra<SFSCredentials>(SFSCredentials.PARM_CREDENTIALS).copy())
        startActivityForResult(selectDirIntent, SELECT_DIR)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SELECT_DIR) {
            if (resultCode == RESULT_OK) {
                this.directoryToImport = data!!.getParcelableExtra<FileWorkflow>(PARM_WORKFLOW_NAME).fileOrDirectory
                this.fileImportPresenter = FileImportPresenterImpl(FileImportModel(intent.getParcelableExtra(SFSCredentials.PARM_CREDENTIALS)), this)
                fileImportPresenter.start()
            } else {
                setResult(RESULT_CANCELED)
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()

    }
}
