package com.vandenbreemen.secretcamera

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.api.Project
import com.vandenbreemen.secretcamera.mvp.projects.ProjectListPresenter
import com.vandenbreemen.secretcamera.mvp.projects.ProjectListRouter
import com.vandenbreemen.secretcamera.mvp.projects.ProjectListView
import dagger.android.AndroidInjection
import javax.inject.Inject

class ProjectViewHolder(val projectView: ViewGroup): RecyclerView.ViewHolder(projectView)

class ProjectAdapter(private val dataSet: List<Project>): RecyclerView.Adapter<ProjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val group = LayoutInflater.from(parent.context).inflate(
                R.layout.project_list_item, parent, false
        ) as ViewGroup

        return ProjectViewHolder(group)

    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val viewGroup = holder.projectView
        viewGroup.findViewById<TextView>(R.id.projectName).text = dataSet[position].title
    }

}



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

        findViewById<RecyclerView>(R.id.projectList).apply {

            layoutManager = LinearLayoutManager(this@ProjectsActivity)

        }
    }

    override fun onResume() {
        super.onResume()

        presenter.start()
    }

    override fun onPause() {
        super.onPause()
    }

    private fun onSubmitProjectDetails(dialog: View) {
        val projectName = dialog.findViewById<EditText>(R.id.projectName)
        val projectDescription = dialog.findViewById<EditText>(R.id.projectDescription)

        val newProject = Project(projectName.text.toString(), projectDescription.text.toString())
        presenter.addProject(newProject)
    }

    fun showCreateProject() {

        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
        val dialog = findViewById<ViewGroup>(R.id.addProjectDialog)

        val okButton = dialog.findViewById<Button>(R.id.ok)
        okButton.setOnClickListener { v ->
            onSubmitProjectDetails(dialog)
        }

        dialog.visibility = VISIBLE
        dialog.startAnimation(animation)

    }

    fun onAddProject(view: View) {
        showCreateProject()
    }

    override fun showProjects(projects: List<Project>) {
        findViewById<RecyclerView>(R.id.projectList).apply {
            adapter = ProjectAdapter(projects)
        }
    }

    override fun onReadyToUse() {
    }

    override fun showError(error: ApplicationError) {
    }

}