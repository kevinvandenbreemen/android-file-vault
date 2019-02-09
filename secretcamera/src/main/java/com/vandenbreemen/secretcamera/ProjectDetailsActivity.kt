package com.vandenbreemen.secretcamera

import android.animation.Animator
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.api.Project
import com.vandenbreemen.secretcamera.api.Task
import com.vandenbreemen.secretcamera.mvp.projects.ProjectDetailsPresenter
import com.vandenbreemen.secretcamera.mvp.projects.ProjectDetailsRouter
import com.vandenbreemen.secretcamera.mvp.projects.ProjectDetailsView
import com.vandenbreemen.secretcamera.ui.DragListener
import com.vandenbreemen.secretcamera.ui.DragUpAndDownHelper
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_project_detail.*
import javax.inject.Inject

class TaskViewHolder(val taskView: ViewGroup) : RecyclerView.ViewHolder(taskView)

class TaskAdapter(var dataSet: MutableList<Task>, private val projectDetailsPresenter: ProjectDetailsPresenter) : RecyclerView.Adapter<TaskViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val group = LayoutInflater.from(parent.context).inflate(
                R.layout.project_task_item, parent, false
        ) as ViewGroup

        return TaskViewHolder(group)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val group = holder.taskView
        val task = dataSet[position]
        group.findViewById<TextView>(R.id.taskDescription).text = task.text

        group.setOnClickListener { v ->
            projectDetailsPresenter.viewTask(task)
        }

        val checkBackground: CardView = group.findViewById<CardView>(R.id.completedContainer)
        val checkbox = group.findViewById<CheckBox>(R.id.completed)

        checkbox.setOnCheckedChangeListener(null)
        checkbox.isChecked = task.complete
        if (task.complete) {
            checkBackground.setCardBackgroundColor(checkbox.resources.getColor(R.color.green))
        } else {
            checkBackground.setCardBackgroundColor(checkbox.resources.getColor(R.color.cardview_light_background))
        }

        checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            projectDetailsPresenter.setCompleted(task, isChecked)
        }
    }


}

class ProjectDetailsActivity: Activity(), ProjectDetailsView, ProjectDetailsRouter {


    companion object {
        val PARM_PROJECT_NAME = "__projectName"
    }

    var screenWidth: Int = 0

    var transitionDistance: Int = 0

    var actionsShowing = false

    val dialogs = mutableListOf<Dialog>()

    @Inject
    lateinit var presenter: ProjectDetailsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_project_detail)

        //  Work out the size of the screen
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        screenWidth = size.x

        //  Move the actions off to the left

        transitionDistance = 50
        actionsButton.translationX = -1 * (screenWidth.toFloat() - transitionDistance)
        descriptionCard.translationX = -1 * (screenWidth.toFloat() - transitionDistance)

        actionsButton.setOnClickListener { v ->
            if (actionsShowing) {
                hideActionsSection()
            } else {
                showActionsSection()
            }
        }

        addTask.setOnClickListener { v ->
            presenter.selectAddTask()
        }

        findViewById<RecyclerView>(R.id.taskList).apply {
            layoutManager = LinearLayoutManager(this@ProjectDetailsActivity)
        }

        findViewById<TextView>(R.id.projectDescription).apply {
            setOnClickListener { v ->
                presenter.selectProjectDetails()
            }
        }

        findViewById<CardView>(R.id.titleCard).apply {
            setOnClickListener { v ->
                presenter.selectProjectDetails()
            }
        }
    }


    override fun displayProjectDetails(project: Project) {
        val builder = AlertDialog.Builder(this)

        val projectDetailsView = layoutInflater.inflate(R.layout.layout_edit_project_details, null)

        projectDetailsView.findViewById<EditText>(R.id.projectDescriptionForEdit).setText(project.details)
        projectDetailsView.findViewById<EditText>(R.id.projectNameForEdit).setText(project.title)

        projectDetailsView.findViewById<Button>(R.id.ok).setOnClickListener { v ->
            presenter.submitUpdatedProjectDetails(
                    projectDetailsView.findViewById<EditText>(R.id.projectNameForEdit).text.toString(),
                    projectDetailsView.findViewById<EditText>(R.id.projectDescriptionForEdit).text.toString()
            )
        }

        builder.setView(projectDetailsView)

        val view: Dialog = builder.create()
        dialogs.add(view)

        projectDetailsView.findViewById<Button>(R.id.cancel).setOnClickListener { v ->
            view.dismiss()
        }

        view.show()
    }

    override fun onResume() {
        super.onResume()

        presenter.start()
    }

    override fun onPause() {
        super.onPause()

        findViewById<ViewGroup>(R.id.overlay).visibility = View.VISIBLE
        dismissAllDialogs()

        presenter.close()

        finish()
    }

    fun showActionsSection() {

        val animationListener = object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                actionsShowing = true
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }

        }

        actionsButton.animate()
                .translationXBy(screenWidth.toFloat() - transitionDistance)
                .setListener(animationListener).duration = 300

        descriptionCard.animate()
                .translationXBy(screenWidth.toFloat() - transitionDistance)
                .setListener(animationListener).duration = 300

    }

    fun hideActionsSection() {
        val animationListener = object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                actionsShowing = false
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }

        }

        actionsButton.animate()
                .translationXBy(-(screenWidth.toFloat() - transitionDistance))
                .setListener(animationListener).duration = 300

        descriptionCard.animate()
                .translationXBy(-(screenWidth.toFloat() - transitionDistance))
                .setListener(animationListener).duration = 300
    }

    override fun showDescription(description: String) {
        runOnUiThread {
            dismissAllDialogs()
            projectDescription.text = description
        }
    }

    override fun showName(title: String) {
        findViewById<TextView>(R.id.projectName).text = title
    }

    override fun onReadyToUse() {
    }

    override fun showError(error: ApplicationError) {
        runOnUiThread {
            Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupDragAndDropForTasks() {
        findViewById<RecyclerView>(R.id.taskList).apply {

            val adapter = this.adapter as TaskAdapter

            val touchHelper = ItemTouchHelper(DragUpAndDownHelper(object : DragListener {
                override fun onViewMoved(oldPosition: Int, newPosition: Int) {
                    val taskAtOldPosition = adapter.dataSet.removeAt(oldPosition)
                    adapter.dataSet.add(newPosition, taskAtOldPosition)
                    adapter.notifyItemMoved(oldPosition, newPosition)
                    presenter.notifyItemMoved(oldPosition, newPosition)
                }
            }))

            touchHelper.attachToRecyclerView(this)
        }
    }

    override fun displayTasks(tasks: List<Task>) {

        runOnUiThread {

            //  Hide all dialogs
            dismissAllDialogs()

            findViewById<RecyclerView>(R.id.taskList).apply {
                val taskList = mutableListOf<Task>()
                taskList.addAll(tasks)
                if (adapter == null) {
                    adapter = TaskAdapter(taskList, presenter)
                    setupDragAndDropForTasks()
                } else {
                    val tasksAdapter = adapter as TaskAdapter
                    tasksAdapter.dataSet = taskList
                    tasksAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun dismissAllDialogs() {
        dialogs.forEach { dialog ->
            dialog.dismiss()
        }

        dialogs.clear()
    }

    override fun showTaskDetails(task: Task?) {

        val builder = AlertDialog.Builder(this)

        val taskDetailView = layoutInflater.inflate(R.layout.layout_task_details, null)

        task?.let { existingTask ->
            taskDetailView.findViewById<EditText>(R.id.taskDescription).setText(existingTask.text)
        }

        taskDetailView.findViewById<Button>(R.id.ok).setOnClickListener { v ->

            task?.let { existingTask ->
                val updatedTask = Task(taskDetailView.findViewById<EditText>(R.id.taskDescription).text.toString())
                presenter.submitUpdateTaskDetails(existingTask, updatedTask)
            } ?: run {
                val task = Task(taskDetailView.findViewById<EditText>(R.id.taskDescription).text.toString())
                presenter.submitTaskDetails(task)
            }


        }

        builder.setView(taskDetailView)

        val view: Dialog = builder.create()
        dialogs.add(view)

        taskDetailView.findViewById<Button>(R.id.cancel).setOnClickListener { v ->
            view.dismiss()
        }

        view.show()
    }

    fun onReturnToMain(view: View) {
        presenter.returnToMain()
    }

    override fun returnToMain(credentials: SFSCredentials) {

        val intent = Intent(this, ProjectsActivity::class.java)
        intent.putExtra(SFSCredentials.PARM_CREDENTIALS, credentials)
        startActivity(intent)

    }
}