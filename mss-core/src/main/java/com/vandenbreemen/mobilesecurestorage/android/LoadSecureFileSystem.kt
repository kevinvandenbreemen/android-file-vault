package com.vandenbreemen.mobilesecurestorage.android

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.vandenbreemen.mobilesecurestorage.R
import com.vandenbreemen.mobilesecurestorage.android.api.FileWorkflow
import com.vandenbreemen.mobilesecurestorage.android.mvp.loadfilesystem.LoadFileSystemController
import com.vandenbreemen.mobilesecurestorage.android.mvp.loadfilesystem.LoadFileSystemModel
import com.vandenbreemen.mobilesecurestorage.android.mvp.loadfilesystem.LoadFileSystemView
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import java.io.File
import java.util.function.Consumer

class LoadSecureFileSystem : Activity(), LoadFileSystemView {

    companion object {
        const val SELECT_FILE = 1
    }

    override fun onLoadSuccess(credentials: SFSCredentials) {
        listener.accept(credentials)
    }

    override fun display(error: ApplicationError) {
        Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    private lateinit var controller: LoadFileSystemController

    private lateinit var fileToLoad: File

    private var listener: Consumer<SFSCredentials> = Consumer { onCredentialsEntered(it) }

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_secure_file_system)

        //  Set up enter key
        findViewById<EditText>(R.id.password)

        val intent = Intent(this, FileSelectActivity::class.java)
        intent.putExtra(FileSelectActivity.PARM_NO_CONFIRM_NEEDED, true)
        intent.putExtra(FileSelectActivity.PARM_TITLE, resources.getText(R.string.select_file))
        startActivityForResult(intent, SELECT_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (SELECT_FILE == requestCode) {
            if (resultCode == RESULT_OK) {
                val tempWorkflow: FileWorkflow = data!!.getParcelableExtra<FileWorkflow>(FileWorkflow.PARM_WORKFLOW_NAME)
                this.fileToLoad = tempWorkflow.fileOrDirectory
                controller = LoadFileSystemController(LoadFileSystemModel(this.fileToLoad), this)
            } else {
                setResult(RESULT_CANCELED)
                finish()
            }

        }
    }

    /**
     * test method
     */
    protected fun setListener(listener: Consumer<SFSCredentials>) {
        this.listener = listener
    }

    private fun onCredentialsEntered(credentials: SFSCredentials) {
        val result = Intent()
        result.putExtra(SFSCredentials.PARM_CREDENTIALS, credentials)
        setResult(RESULT_OK, result)
        finish()
    }

    fun onOkay(view: View) {
        val passView = findViewById<TextView>(R.id.password)
        controller.providePassword(passView.text.toString())
    }

    fun onCancel(view: View) {
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_ENTER -> {
                val passView = findViewById<TextView>(R.id.password)
                controller.providePassword(passView.text.toString())
                return true
            }
            else -> return super.onKeyUp(keyCode, event)
        }
    }
}
