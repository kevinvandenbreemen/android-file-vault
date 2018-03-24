package com.vandenbreemen.secretcamera

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.mvp.impl.notes.NoteDetailsModel
import com.vandenbreemen.secretcamera.mvp.impl.notes.NoteDetailsPresenterImpl
import com.vandenbreemen.secretcamera.mvp.notes.NoteDetailsPresenter
import com.vandenbreemen.secretcamera.mvp.notes.NoteDetailsView

class NoteDetailsActivity : Activity(), NoteDetailsView {
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

    private lateinit var presenter:NoteDetailsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)

        val stringSelection = intent.getParcelableExtra<StringSelection>(SELECTED_STRING)!!
        this.presenter = NoteDetailsPresenterImpl(
                NoteDetailsModel(stringSelection.credentials!!, stringSelection.selectedString),
                this
        )
    }

    override fun onResume() {
        super.onResume()
        this.presenter.start()
    }
}
