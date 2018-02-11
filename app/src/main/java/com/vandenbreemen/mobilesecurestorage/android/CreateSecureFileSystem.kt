package com.vandenbreemen.mobilesecurestorage.android

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.vandenbreemen.mobilesecurestorage.R
import com.vandenbreemen.mobilesecurestorage.android.api.FileWorkflow
import com.vandenbreemen.mobilesecurestorage.android.mvp.createfilesystem.CreateSecureFileSystemController
import com.vandenbreemen.mobilesecurestorage.android.mvp.createfilesystem.CreateSecureFileSystemModel
import com.vandenbreemen.mobilesecurestorage.android.mvp.createfilesystem.CreateSecureFileSystemView
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem

class CreateSecureFileSystem : Activity(), CreateSecureFileSystemView {
    override fun onComplete(sfs: SecureFileSystem?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun display(error: ApplicationError) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var controller: CreateSecureFileSystemController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_secure_file_system)

        val newFileWorkflow: FileWorkflow? = intent.getParcelableExtra("Workflow") as FileWorkflow?
        val model = CreateSecureFileSystemModel(newFileWorkflow!!.fileOrDirectory)

        this.controller = CreateSecureFileSystemController(model, this)
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
}
