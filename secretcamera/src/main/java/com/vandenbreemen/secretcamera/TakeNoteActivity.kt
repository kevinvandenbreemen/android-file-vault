package com.vandenbreemen.secretcamera

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.mvp.impl.TakeNewNoteModel
import com.vandenbreemen.secretcamera.mvp.impl.TakeNewNotePresenterImpl
import com.vandenbreemen.secretcamera.mvp.notes.TakeNewNotePresenter
import com.vandenbreemen.secretcamera.mvp.notes.TakeNewNoteView

class TakeNoteActivity : Activity(), TakeNewNoteView {
    override fun onReadyToUse() {
    }

    override fun showError(error: ApplicationError) {
        Toast.makeText(this, error.localizedMessage, LENGTH_SHORT).show()
    }

    override fun onNoteSucceeded(message: String) {

    }

    override fun close() {
        val backToMain = presenter.getNewActivityIntent(this, MainActivity::class.java)
        startActivity(backToMain)
        finish()
    }

    lateinit var credentials: SFSCredentials

    lateinit var presenter: TakeNewNotePresenter

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_note)

        this.credentials = intent.getParcelableExtra(SFSCredentials.PARM_CREDENTIALS) as SFSCredentials
        this.presenter = TakeNewNotePresenterImpl(this, TakeNewNoteModel(credentials))
    }

    fun onCancel(view: View) {
        presenter.onCancel()
    }

    override fun onPause() {
        super.onPause()
        presenter.close()
    }

    @SuppressLint("WrongViewCast")
    fun onOk(view: View) {
        presenter.provideNoteDetails(findViewById<EditText>(R.id.title).text.toString(),
                findViewById<EditText>(R.id.content).text.toString()
        )
    }


}
