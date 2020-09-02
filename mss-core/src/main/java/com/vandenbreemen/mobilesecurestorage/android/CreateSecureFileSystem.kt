package com.vandenbreemen.mobilesecurestorage.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.vandenbreemen.mobilesecurestorage.R
import com.vandenbreemen.mobilesecurestorage.android.api.FileWorkflow
import com.vandenbreemen.mobilesecurestorage.android.mvp.createfilesystem.CreateSecureFileSystemController
import com.vandenbreemen.mobilesecurestorage.android.mvp.createfilesystem.CreateSecureFileSystemModel
import com.vandenbreemen.mobilesecurestorage.android.mvp.createfilesystem.CreateSecureFileSystemView
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.test.BackgroundCompletionCallback
import java.util.function.Consumer

class CreateSecureFileSystem : Activity(), CreateSecureFileSystemView {

    companion object {
        const val SELECT_DIR = 2
        var sfsLoadedCallback: BackgroundCompletionCallback? = null
    }

    /**
     * Capture created secure file system
     */
    private var onCompleteListener: Consumer<SFSCredentials> = Consumer { doNextStep(it) }

    override fun onComplete(credentials: SFSCredentials) {
        onCompleteListener.accept(credentials)
    }

    override fun display(error: ApplicationError) {
        Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    private lateinit var controller: CreateSecureFileSystemController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_secure_file_system)

        val intent = Intent(this, FileSelectActivity::class.java)
        intent.putExtra(FileSelectActivity.PARM_DIR_ONLY, true)
        intent.putExtra(FileSelectActivity.PARM_TITLE, resources.getText(R.string.loc_for_new_sfs))
        startActivityForResult(intent, SELECT_DIR)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SELECT_DIR) {
            if (resultCode == RESULT_OK) {
                data?.let { d->
                    (d.getParcelableExtra<FileWorkflow>(FileWorkflow.PARM_WORKFLOW_NAME) as? FileWorkflow)?.let { fileWorkflow ->
                        val tempWorkflow: FileWorkflow = fileWorkflow
                        val model = CreateSecureFileSystemModel(tempWorkflow.fileOrDirectory)
                        this.controller = CreateSecureFileSystemController(model, this)
                    }

                }

            } else {
                setResult(RESULT_CANCELED)
                finish()
            }
        }
    }

    fun onCancel(view: View) {
        setResult(RESULT_CANCELED)
        finish()
    }

    fun onOkay(view: View) {
        sfsLoadedCallback?.let { it.onStart() }
        onProvidedDetails()
    }

    private fun onProvidedDetails() {
        val fileName = findViewById<TextView>(R.id.fileName)
        val pass = findViewById<TextView>(R.id.password)
        val cPass = findViewById<TextView>(R.id.confirmPassword)

        controller.submitNewSFSDetails(
                fileName.text.toString(),
                pass.text.toString(),
                cPass.text.toString()
        )
    }

    private fun doNextStep(credentials: SFSCredentials) {
        val result = Intent()
        result.putExtra(SFSCredentials.PARM_CREDENTIALS, credentials)
        setResult(RESULT_OK, result)
        finish()
    }

    /**
     * For testing only - override what happens when sfs created successfully
     */
    protected fun setOnCompleteListener(listener: Consumer<SFSCredentials>) {
        this.onCompleteListener = listener
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_ENTER -> {
                onProvidedDetails()
                return true
            }
            else -> return super.onKeyUp(keyCode, event)
        }
    }
}
