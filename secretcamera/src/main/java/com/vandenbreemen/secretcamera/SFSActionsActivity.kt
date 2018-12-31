package com.vandenbreemen.secretcamera

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions.SFSActionsPresenter
import com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions.SFSActionsRouter
import com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions.SFSActionsView
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.test.BackgroundCompletionCallback
import dagger.android.AndroidInjection
import javax.inject.Inject

/**
 *
 * @author kevin
 */
class SFSActionsActivity : Activity(), SFSActionsView, SFSActionsRouter {

    companion object {
        var loading: BackgroundCompletionCallback? = null
    }

    @Inject
    lateinit var presenter: SFSActionsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_sfs_actions)
    }

    fun onClickChangePassword(view: View) {
        presenter.selectChangePassword()
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onReadyToUse() {

    }

    override fun showError(error: ApplicationError) {
        runOnUiThread {
            Toast.makeText(this, error.localizedMessage, LENGTH_LONG).show()
        }
    }

    override fun returnToMain(sfsCredentials: SFSCredentials) {

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(SFSCredentials.PARM_CREDENTIALS, sfsCredentials)
        startActivity(intent)
        finish()
    }

    override fun onPause() {
        super.onPause()
        presenter.close()
    }

    override fun openChangePassword() {
        findViewById<ViewGroup>(R.id.incl_chane_pass_details).visibility = VISIBLE
    }

    fun onEnterChangePasswordDetails(view: View) {
        val currentPassword = findViewById<EditText>(R.id.currentPassword).text.toString()
        val newPassword = findViewById<EditText>(R.id.newPassword).text.toString()
        val reEnter = findViewById<EditText>(R.id.reEnterNewPassword).text.toString()

        loading?.let { it.onStart() }
        presenter.changePassword(currentPassword, newPassword, reEnter)
    }

    override fun setProgressMax(max: Long) {
        runOnUiThread {
            findViewById<ViewGroup>(R.id.progressContainer).visibility = VISIBLE
            findViewById<ViewGroup>(R.id.progressContainer).findViewById<ProgressBar>(R.id.progressBar).max = max.toInt()
        }

    }

    override fun setCurrentProgress(currentProgress: Long) {
        runOnUiThread {
            findViewById<ViewGroup>(R.id.progressContainer).findViewById<ProgressBar>(R.id.progressBar).progress = currentProgress.toInt()
        }
    }
}