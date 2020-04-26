package com.vandenbreemen.secretcamera

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.recyclerview.widget.LinearLayoutManager
import com.vandenbreemen.kevindesignsystem.views.KDSSystemActivity
import com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions.FileListItemView
import com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions.SFSActionsPresenter
import com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions.SFSActionsRouter
import com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions.SFSActionsView
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.di.injectSFSActions
import com.vandenbreemen.test.BackgroundCompletionCallback
import kotlinx.android.synthetic.main.layout_sfs_list.*
import javax.inject.Inject

/**
 *
 * @author kevin
 */
class SFSActionsActivity : KDSSystemActivity(), SFSActionsView, SFSActionsRouter {

    companion object {
        var loading: BackgroundCompletionCallback? = null
    }

    @Inject
    lateinit var presenter: SFSActionsPresenter

    lateinit var adapter: ListFilesAdapter

    val filesList: MutableList<FileListItemView> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        injectSFSActions(this)
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        findViewById<View>(android.R.id.content)
                .setBackgroundColor(getColor(R.color.kds_background_default))

        findViewById<ViewGroup>(com.vandenbreemen.kevindesignsystem.R.id.mainContent)
                .addView(layoutInflater.inflate(R.layout.activity_sfs_actions,
                        findViewById<ViewGroup>(com.vandenbreemen.kevindesignsystem.R.id.mainContent),
                        false
                ))

        val viewManager = LinearLayoutManager(this)
        this.adapter = ListFilesAdapter(filesList, FileTypeIconDrawableProvider(this))
        fileListRecyclerView.adapter = this.adapter
        fileListRecyclerView.layoutManager = viewManager
    }

    fun onClickChangePassword(view: View) {
        presenter.selectChangePassword()
    }

    fun onCancelChangePassword(view: View) {
        presenter.cancelChangePassword()
    }

    override fun closeChangePassword() {
        findViewById<ViewGroup>(R.id.incl_chane_pass_details).visibility = GONE
        findViewById<EditText>(R.id.currentPassword).setText("")
        findViewById<EditText>(R.id.newPassword).setText("")
        findViewById<EditText>(R.id.reEnterNewPassword).setText("")
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

    override fun displayFileList(files: List<FileListItemView>) {
        filesList.clear()
        filesList.addAll(files)
        adapter.notifyDataSetChanged()
    }
}