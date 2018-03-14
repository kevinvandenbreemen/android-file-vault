package com.vandenbreemen.secretcamera

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.mvp.impl.TakeNewNoteModel
import com.vandenbreemen.secretcamera.mvp.impl.TakeNewNotePresenterImpl
import com.vandenbreemen.secretcamera.mvp.notes.TakeNewNotePresenter
import com.vandenbreemen.secretcamera.mvp.notes.TakeNewNoteView

class TakeNoteActivity : Activity(), TakeNewNoteView {
    override fun showError(error: ApplicationError) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNoteSucceeded(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        presenter.close()
    }

    override fun close() {
        val backToMain = Intent(this, MainActivity::class.java)
        backToMain.putExtra(SFSCredentials.PARM_CREDENTIALS, credentials)
        startActivity(backToMain)
        finish()
    }

    lateinit var credentials: SFSCredentials

    lateinit var presenter: TakeNewNotePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_note)

        this.credentials = intent.getParcelableExtra(SFSCredentials.PARM_CREDENTIALS) as SFSCredentials
        this.presenter = TakeNewNotePresenterImpl(this, TakeNewNoteModel(credentials))
    }

    fun onCancel(view: View) {
        presenter.onCancel()
    }

    @SuppressLint("WrongViewCast")
    fun onOk(view: View) {
        presenter.provideNoteDetails(findViewById<EditText>(R.id.title).text.toString(),
                findViewById<EditText>(R.id.content).text.toString()
        )
    }


}
