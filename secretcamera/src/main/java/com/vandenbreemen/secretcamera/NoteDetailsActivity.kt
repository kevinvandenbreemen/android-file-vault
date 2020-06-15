package com.vandenbreemen.secretcamera

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.api.Note
import com.vandenbreemen.secretcamera.di.ActivitySecurity
import com.vandenbreemen.secretcamera.di.injectNoteDetails
import com.vandenbreemen.secretcamera.mvp.notes.NoteDetailsPresenter
import com.vandenbreemen.secretcamera.mvp.notes.NoteDetailsView
import javax.inject.Inject

class NoteDetailsActivity : Activity(), NoteDetailsView {

    lateinit var noteTextReadonly: String

    override fun enableEdit() {
        findViewById<EditText>(R.id.title).isEnabled = true

        val container = findViewById<ViewGroup>(R.id.contentContainer)
        container.removeAllViews()
        val noteEditor: EditText = layoutInflater.inflate(R.layout.text_edit, container, false) as EditText
        noteEditor.setText(noteTextReadonly)
        container.addView(noteEditor)
    }

    @SuppressLint("WrongViewCast")
    override fun getNoteOnUI(): Note {
        return Note((findViewById(R.id.title) as EditText).text.toString(),
                findViewById<EditText>(R.id.content).text.toString())
    }

    @Inject
    lateinit var presenter:NoteDetailsPresenter

    override fun setNoteTitle(title: String) {
        findViewById<EditText>(R.id.title).setText(title)
    }

    override fun setNoteContent(content: String) {
        noteTextReadonly = content
        val container = findViewById<ViewGroup>(R.id.contentContainer)
        container.removeAllViews()
        val scrollView = layoutInflater.inflate(R.layout.text_readonly, container, false)
        scrollView.findViewById<TextView>(R.id.content).setText(content)
        container.addView(scrollView)
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
        injectNoteDetails(this)
        super.onCreate(savedInstanceState)
        ActivitySecurity.setSecurity(this)
        setContentView(R.layout.activity_note_details)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    override fun onResume() {
        super.onResume()
        this.presenter.start()
    }

    fun onOkay(view:View){

        //  See also https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = getCurrentFocus()
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view!!.getWindowToken(), 0)

        presenter.onOk()

    }

    fun onEdit(view: View) {
        presenter.onEdit()
    }

    override fun close(credentials: SFSCredentials) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(SFSCredentials.PARM_CREDENTIALS, credentials)
        startActivity(intent)
        finish()
    }
}
