package com.vandenbreemen.secretcamera

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.api.Note
import com.vandenbreemen.secretcamera.mvp.notes.NoteDetailsPresenter
import com.vandenbreemen.secretcamera.mvp.notes.NoteDetailsView
import dagger.android.AndroidInjection
import javax.inject.Inject

class NoteDetailsActivity : Activity(), NoteDetailsView {
    override fun enableEdit() {
        findViewById<EditText>(R.id.title).isEnabled = true
        findViewById<EditText>(R.id.content).isEnabled = true
    }

    @SuppressLint("WrongViewCast")
    override fun getNoteOnUI(): Note {
        return Note(findViewById<EditText>(R.id.title).text.toString(),
                findViewById<EditText>(R.id.content).text.toString())
    }

    @Inject
    lateinit var presenter:NoteDetailsPresenter

    override fun setNoteTitle(title: String) {
        findViewById<EditText>(R.id.title).setText(title)
    }

    override fun setNoteContent(content: String) {
        findViewById<EditText>(R.id.content).setText(content)
    }

    override fun onReadyToUse() {
        findViewById<ViewGroup>(R.id.overlay).visibility = GONE
    }

    override fun showError(error: ApplicationError) {
        Toast.makeText(this, error.localizedMessage, LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        findViewById<ViewGroup>(R.id.overlay).visibility = VISIBLE
        presenter.close()
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

    fun onOkay(view:View){
        presenter.onOk()
    }

    fun onEdit(view: View) {
        presenter.onEdit()
    }

    override fun close() {
        val intent = presenter.getNewActivityIntent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
