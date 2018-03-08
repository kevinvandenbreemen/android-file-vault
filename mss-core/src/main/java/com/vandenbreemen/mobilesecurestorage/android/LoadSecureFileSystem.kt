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
import com.vandenbreemen.mobilesecurestorage.android.mvp.loadfilesystem.LoadFileSystemController
import com.vandenbreemen.mobilesecurestorage.android.mvp.loadfilesystem.LoadFileSystemModel
import com.vandenbreemen.mobilesecurestorage.android.mvp.loadfilesystem.LoadFileSystemView
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import java.util.function.Consumer

class LoadSecureFileSystem : Activity(), LoadFileSystemView {
    override fun onLoadSuccess(credentials: SFSCredentials) {
        listener.accept(credentials)
    }

    override fun display(error: ApplicationError) {
        Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    private lateinit var controller: LoadFileSystemController

    private var listener: Consumer<SFSCredentials> = Consumer { onCredentialsEntered(it) }

    private lateinit var workflow: FileWorkflow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_secure_file_system)

        this.workflow = intent.getParcelableExtra<FileWorkflow>(FileWorkflow.PARM_WORKFLOW_NAME)
        controller = LoadFileSystemController(LoadFileSystemModel(workflow.fileOrDirectory), this)
    }

    /**
     * test method
     */
    protected fun setListener(listener: Consumer<SFSCredentials>) {
        this.listener = listener
    }

    private fun onCredentialsEntered(credentials: SFSCredentials) {
        val workflow = intent.getParcelableExtra<FileWorkflow>(FileWorkflow.PARM_WORKFLOW_NAME)
        if (workflow.activityToStartAfterTargetActivityFinished != null) {
            val startNextActivity = Intent(this, workflow.activityToStartAfterTargetActivityFinished)
            startNextActivity.putExtra(SFSCredentials.PARM_CREDENTIALS, credentials)
            startActivity(startNextActivity)
            finish()
            return
        }
        Log.w("LoadSecureFileSystem", "Credentials successfully provided but ${workflow.activityToStartAfterTargetActivityFinished} is null")
    }

    fun onOkay(view: View) {
        val passView = findViewById<TextView>(R.id.password)
        controller.providePassword(passView.text.toString())
    }

    fun onCancel(view: View) {
        handleWorkflowCancel(this, workflow)
    }
}
