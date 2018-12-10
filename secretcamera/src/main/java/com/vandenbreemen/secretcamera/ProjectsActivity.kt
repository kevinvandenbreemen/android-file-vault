package com.vandenbreemen.secretcamera

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.api.Project
import com.vandenbreemen.secretcamera.mvp.projects.ProjectListPresenter
import com.vandenbreemen.secretcamera.mvp.projects.ProjectListRouter
import com.vandenbreemen.secretcamera.mvp.projects.ProjectListView
import dagger.android.AndroidInjection
import javax.inject.Inject

/**
 *
 * @author kevin
 */
class ProjectsActivity : Activity(), ProjectListView, ProjectListRouter {

    @Inject
    lateinit var presenter: ProjectListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_projects)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    fun showCreateProject() {

        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
        val dialog = findViewById<ViewGroup>(R.id.addProjectDialog)
        dialog.visibility = VISIBLE
        dialog.startAnimation(animation)

    }

    fun onAddProject(view: View) {
        showCreateProject()
    }

    override fun showProjects(projects: List<Project>) {
    }

    override fun onReadyToUse() {
    }

    override fun showError(error: ApplicationError) {
    }

}