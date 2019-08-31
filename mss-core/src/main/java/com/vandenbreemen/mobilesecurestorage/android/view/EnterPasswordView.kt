package com.vandenbreemen.mobilesecurestorage.android.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import com.vandenbreemen.mobilesecurestorage.R
import com.vandenbreemen.mobilesecurestorage.android.mvp.loadfilesystem.LoadFileSystemController
import com.vandenbreemen.mobilesecurestorage.android.mvp.loadfilesystem.LoadFileSystemModel
import com.vandenbreemen.mobilesecurestorage.android.mvp.loadfilesystem.LoadFileSystemView
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import java.io.File

class EnterPasswordView(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs), LoadFileSystemView {

    lateinit var controller: LoadFileSystemController

    lateinit var successFullyEnteredPassword: (SFSCredentials)->Unit

    init {
        context?.let {ctx->

            val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.reload_sfs_file, this)

        }
    }

    fun promptForPasswordOnResume(fileLocation: File, onSuccess: (SFSCredentials)->Unit, onCancel: ()->Unit) {
        controller = LoadFileSystemController(LoadFileSystemModel(fileLocation), this)
        successFullyEnteredPassword = onSuccess

        findViewById<Button>(R.id.ok).setOnClickListener{v->
            val passwordView = findViewById<EditText>(R.id.password)
            controller.providePassword(passwordView.text.toString())

            val errorTextView = findViewById<TextView>(R.id.errorMessage)
            errorTextView.visibility = View.GONE

        }

        findViewById<Button>(R.id.cancel).setOnClickListener { v->onCancel() }

        visibility = View.VISIBLE
    }

    override fun onLoadSuccess(credentials: SFSCredentials) {
        successFullyEnteredPassword(credentials)
    }

    override fun display(error: ApplicationError) {
        val errorTextView = findViewById<TextView>(R.id.errorMessage)
        errorTextView.text = error.localizedMessage

        errorTextView.alpha = 0.0f
        errorTextView.visibility = View.VISIBLE
        errorTextView.animate().alpha(1.0f).setDuration(200).start()


    }

}