package com.vandenbreemen.secretcamera

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions.SFSActionsPresenter
import com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions.SFSActionsRouter
import com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions.SFSActionsView
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import dagger.android.AndroidInjection
import javax.inject.Inject

/**
 *
 * @author kevin
 */
class SFSActionsActivity : Activity(), SFSActionsView, SFSActionsRouter {

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

    override fun onReadyToUse() {

    }

    override fun showError(error: ApplicationError) {
        runOnUiThread {
            Toast.makeText(this, error.localizedMessage, LENGTH_LONG).show()
        }
    }

    override fun returnToMain(sfsCredentials: SFSCredentials) {

    }

    override fun openChangePassword() {
        findViewById<ViewGroup>(R.id.incl_chane_pass_details).visibility = VISIBLE
    }
}