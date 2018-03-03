package com.vandenbreemen.mobilesecurestorage.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import java.util.function.Consumer

class CreateSecureFileSystem : Activity(), CreateSecureFileSystemView {

    /**
     * Capture created secure file system
     */
    private var onCompleteListener: Consumer<SFSCredentials> = Consumer { doNextStep(it) }

    private lateinit var workflow: FileWorkflow

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

        val newFileWorkflow: FileWorkflow? = intent.getParcelableExtra<FileWorkflow>(FileWorkflow.PARM_WORKFLOW_NAME) as FileWorkflow?
        val model = CreateSecureFileSystemModel(newFileWorkflow!!.fileOrDirectory)
        this.workflow = newFileWorkflow

        this.controller = CreateSecureFileSystemController(model, this)
    }

    fun onCancel(view: View) {
        handleWorkflowCancel(this, workflow)
    }

    fun onOkay(view: View) {

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
        val newFileWorkflow: FileWorkflow? = intent.getParcelableExtra<FileWorkflow>(FileWorkflow.PARM_WORKFLOW_NAME) as FileWorkflow?
        if (newFileWorkflow!!.activityToStartAfterTargetActivityFinished != null) {
            val nextActivity = Intent(this, newFileWorkflow!!.activityToStartAfterTargetActivityFinished)
            nextActivity.putExtra(SFSCredentials.PARM_CREDENTIALS, credentials)
            startActivity(nextActivity)
            finish()
            return
        }
        Log.w("CreateSecureFileSystem", "No final activity once SFS created")
    }

    /**
     * For testing only - override what happens when sfs created successfully
     */
    protected fun setOnCompleteListener(listener: Consumer<SFSCredentials>) {
        this.onCompleteListener = listener
    }
}
