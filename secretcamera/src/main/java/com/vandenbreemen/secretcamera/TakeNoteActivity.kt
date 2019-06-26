package com.vandenbreemen.secretcamera

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.di.injectTakeNote
import com.vandenbreemen.secretcamera.mvp.notes.TakeNewNotePresenter
import com.vandenbreemen.secretcamera.mvp.notes.TakeNewNoteView
import javax.inject.Inject

class TakeNoteActivity : Activity(), TakeNewNoteView {

    @Inject
    lateinit var presenter: TakeNewNotePresenter

    override fun onReadyToUse() {
        findViewById<ViewGroup>(R.id.overlay).visibility = GONE
    }

    override fun showError(error: ApplicationError) {
        Toast.makeText(this, error.localizedMessage, LENGTH_SHORT).show()
    }

    override fun onNoteSucceeded(message: String) {

    }

    override fun close(credentials: SFSCredentials) {
        val backToMain = Intent(this, MainActivity::class.java)
        backToMain.putExtra(SFSCredentials.PARM_CREDENTIALS, credentials)
        startActivity(backToMain)
        finish()
    }


    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        injectTakeNote(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_note)

        findViewById<ViewGroup>(R.id.overlay).visibility = VISIBLE
    }

    fun onCancel(view: View) {
        presenter.onCancel()
    }

    override fun onPause() {
        super.onPause()
        findViewById<ViewGroup>(R.id.overlay).visibility = VISIBLE
        presenter.saveAndClose(findViewById<EditText>(R.id.title).text.toString(),
                findViewById<EditText>(R.id.content).text.toString()
        ).subscribe()
    }

    @SuppressLint("WrongViewCast")
    fun onOk(view: View) {
        presenter.provideNoteDetails(findViewById<EditText>(R.id.title).text.toString(),
                findViewById<EditText>(R.id.content).text.toString()
        )
    }


}
