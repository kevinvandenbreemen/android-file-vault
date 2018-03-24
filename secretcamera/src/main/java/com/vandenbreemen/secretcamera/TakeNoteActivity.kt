package com.vandenbreemen.secretcamera

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.mvp.notes.TakeNewNotePresenter
import com.vandenbreemen.secretcamera.mvp.notes.TakeNewNoteView
import dagger.android.AndroidInjection
import javax.inject.Inject

class TakeNoteActivity : Activity(), TakeNewNoteView {

    @Inject
    lateinit var presenter: TakeNewNotePresenter

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


    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_note)
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
