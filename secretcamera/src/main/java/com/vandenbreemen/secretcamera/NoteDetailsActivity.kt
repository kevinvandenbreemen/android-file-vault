package com.vandenbreemen.secretcamera

import android.app.Activity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.mvp.notes.NoteDetailsPresenter
import com.vandenbreemen.secretcamera.mvp.notes.NoteDetailsView
import dagger.android.AndroidInjection
import javax.inject.Inject

class NoteDetailsActivity : Activity(), NoteDetailsView {

    @Inject
    lateinit var presenter:NoteDetailsPresenter

    override fun setNoteTitle(title: String) {
        findViewById<EditText>(R.id.title).setText(title)
    }

    override fun setNoteContent(content: String) {
        findViewById<EditText>(R.id.content).setText(content)
    }

    override fun onReadyToUse() {

    }

    override fun showError(error: ApplicationError) {
        Toast.makeText(this, error.localizedMessage, LENGTH_SHORT)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)
    }

    override fun onResume() {
        super.onResume()
        this.presenter.start()
    }
}
