package com.vandenbreemen.secretcamera

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.api.Project
import com.vandenbreemen.secretcamera.di.injectProjectsActivithy
import com.vandenbreemen.secretcamera.mvp.projects.ProjectListPresenter
import com.vandenbreemen.secretcamera.mvp.projects.ProjectListRouter
import com.vandenbreemen.secretcamera.mvp.projects.ProjectListView
import javax.inject.Inject

class ProjectViewHolder(val projectView: ViewGroup): RecyclerView.ViewHolder(projectView)

class ProjectAdapter(private val dataSet: List<Project>, private val projectListPresenter: ProjectListPresenter): RecyclerView.Adapter<ProjectViewHolder>() {

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
        viewGroup.findViewById<TextView>(R.id.projectDescription).text = dataSet[position].details

        viewGroup.setOnClickListener{ v->
            projectListPresenter.viewProjectDetails(dataSet[position])
        }
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
        injectProjectsActivithy(this)
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_projects)

        findViewById<RecyclerView>(R.id.projectList).apply {

            layoutManager = LinearLayoutManager(this@ProjectsActivity)

        }
    }

    override fun gotoProjectDetails(projectName: String, credentials: SFSCredentials) {
        val intent = Intent(this, ProjectDetailsActivity::class.java)
        intent.putExtra(SFSCredentials.PARM_CREDENTIALS, credentials)
        intent.putExtra(ProjectDetailsActivity.PARM_PROJECT_NAME, projectName)

        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()

        presenter.start()
    }

    override fun onPause() {
        super.onPause()

        findViewById<ViewGroup>(R.id.overlay).visibility = View.VISIBLE
        presenter.close()
        finish()
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

        dialog.findViewById<EditText>(R.id.projectName).text.clear()
        dialog.findViewById<EditText>(R.id.projectDescription).text.clear()

        val okButton = dialog.findViewById<Button>(R.id.ok)
        okButton.setOnClickListener { v ->
            onSubmitProjectDetails(dialog)
        }

        dialog.findViewById<Button>(R.id.cancel).setOnClickListener { v ->
            hideCreateProject()
        }

        dialog.visibility = VISIBLE
        dialog.startAnimation(animation)

    }

    fun onAddProject(view: View) {
        showCreateProject()
    }

    override fun showProjects(projects: List<Project>) {
        findViewById<RecyclerView>(R.id.projectList).apply {
            adapter = ProjectAdapter(projects, presenter)
        }
        hideCreateProject()
    }

    private fun hideCreateProject() {
        val dialog = findViewById<ViewGroup>(R.id.addProjectDialog)
        if(dialog.visibility == GONE){
            return
        }

        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out)
        animation.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                dialog.visibility = GONE
            }

            override fun onAnimationStart(animation: Animation?) {

            }
        })

        dialog.startAnimation(animation)
    }

    override fun onReadyToUse() {
    }

    override fun showError(error: ApplicationError) {
        runOnUiThread {
            Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

}