package com.vandenbreemen.secretcamera

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import com.vandenbreemen.mobilesecurestorage.android.fragment.SFSNavFragment
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.android.view.EnterPasswordView
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Pausable
import com.vandenbreemen.secretcamera.mvp.SFSMenuContract
import com.vandenbreemen.secretcamera.mvp.impl.SFSMainMenuModel
import com.vandenbreemen.secretcamera.mvp.impl.SFSMainMenuPresenterImpl
import com.vandenbreemen.test.BackgroundCompletionCallback
import java.io.File

class MainActivity : AppCompatActivity(), SFSMenuContract.SFSMainMenuView, Pausable {

    companion object {
        var sfsLoadedCallback: BackgroundCompletionCallback? = null
    }

    override fun onReadyToUse() {

    }

    override fun showError(error: ApplicationError) {
        Toast.makeText(this, error.localizedMessage, LENGTH_SHORT)
    }

    var mainMenuPresenter: SFSMenuContract.SFSMainMenuPresenter? = null

    override fun onPause() {
        super.onPause()
        mainMenuPresenter?.let {
            it.pause()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //// Removed androidInjection stuff
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        intent.getParcelableExtra<SFSCredentials>(SFSCredentials.PARM_CREDENTIALS)?.let { credentials ->
            val mainSection = findViewById(R.id.mainSection) as ViewGroup
            mainSection.addView(
                    layoutInflater.inflate(R.layout.main_screen_selections, mainSection, false))

            mainMenuPresenter = SFSMainMenuPresenterImpl(SFSMainMenuModel(credentials), this)


        } ?: run {
            val frag = SFSNavFragment()
            fragmentManager.beginTransaction().add(R.id.upperSection, frag).commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SFSNavFragment.GET_CREDENTIALS_ACTION) {
            if (resultCode == RESULT_OK) {
                val credentials = data!!.getParcelableExtra<SFSCredentials>(SFSCredentials.PARM_CREDENTIALS)
                val viewGroup = findViewById(R.id.mainSection) as ViewGroup
                viewGroup.addView(
                        layoutInflater.inflate(R.layout.main_screen_selections, viewGroup, false))

                (findViewById(R.id.upperSection) as ViewGroup).removeAllViews()

                mainMenuPresenter = SFSMainMenuPresenterImpl(SFSMainMenuModel(credentials), this)
                mainMenuPresenter!!.start()
            } else {
                startActivity(intent)
            }
        }
    }

    override fun pauseWithFileOpen(fileLocation: File) {
        val overlay = findViewById<ViewGroup>(R.id.overlay)
        val enterPasswordView = overlay.findViewById<EnterPasswordView>(R.id.enter_password_view)
        enterPasswordView.promptForPasswordOnResume(fileLocation, {sfsCredentials ->
            val presenter = SFSMainMenuPresenterImpl(SFSMainMenuModel(sfsCredentials), this)
            this.mainMenuPresenter = presenter
            overlay.visibility = View.GONE
            presenter.start()
        }, {finish()})
        overlay.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        mainMenuPresenter?.let {
            it.start()
            sfsLoadedCallback ?. let { sfsLoadedCallback!!.onFinish() }
        }
    }

    fun onTakeNote(view:View){
        mainMenuPresenter!!.takeNote()
    }

    override fun gotoNotesList(credentials:SFSCredentials, strings:ArrayList<String>) {

        val workflow = StringSelectorWorkflow(NoteDetailsActivity::class.java, strings, credentials)
        workflow.setOnCancelActivity(MainActivity::class.java)
        val intent = Intent(this, StringSelectorActivity::class.java)
        intent.putExtra(StringSelectorActivity.WORKFLOW, workflow)
        startActivity(intent)
        finish()

    }

    fun onViewPictures(view: View) {
        mainMenuPresenter!!.openGallery()
    }

    fun onViewNotes(view: View){
        mainMenuPresenter!!.viewNotes()
    }

    fun onTakePicture(view: View) {
        mainMenuPresenter!!.takePicture()
    }

    fun onSelectProjects(view: View) {
        mainMenuPresenter!!.openProjects()
    }

    fun onSelectActions(view: View) {
        mainMenuPresenter!!.openActions()
    }

    override fun openActions(credentials: SFSCredentials) {
        val toActions = Intent(this, SFSActionsActivity::class.java)
        toActions.putExtra(SFSCredentials.PARM_CREDENTIALS, credentials)
        startActivity(toActions)
        finish()
    }

    override fun goToProjects(credentials: SFSCredentials) {
        val projects = Intent(this, ProjectsActivity::class.java)
        projects.putExtra(SFSCredentials.PARM_CREDENTIALS, credentials)
        startActivity(projects)
        finish()
    }

    override fun gotoTakeNote(credentials: SFSCredentials) {
        val takeNote = Intent(this, TakeNoteActivity::class.java)
        takeNote.putExtra(SFSCredentials.PARM_CREDENTIALS, credentials)
        startActivity(takeNote)
        finish()
    }

    override fun gotoTakePicture(credentials: SFSCredentials) {
        val takePicture = Intent(this, TakePictureActivity::class.java)
        takePicture.putExtra(SFSCredentials.PARM_CREDENTIALS, credentials)
        startActivity(takePicture)
        finish()
    }

    override fun gotoGallery(credentials: SFSCredentials) {
        val gallery = Intent(this, Gallery::class.java)
        gallery.putExtra(SFSCredentials.PARM_CREDENTIALS, credentials)
        startActivity(gallery)
        finish()
    }
}
