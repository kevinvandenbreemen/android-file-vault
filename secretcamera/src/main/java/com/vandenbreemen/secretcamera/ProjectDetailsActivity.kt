package com.vandenbreemen.secretcamera

import android.animation.Animator
import android.app.Activity
import android.graphics.Point
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.api.Task
import com.vandenbreemen.secretcamera.mvp.projects.ProjectDetailsPresenter
import com.vandenbreemen.secretcamera.mvp.projects.ProjectDetailsRouter
import com.vandenbreemen.secretcamera.mvp.projects.ProjectDetailsView
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_project_detail.*
import javax.inject.Inject

class ProjectDetailsActivity: Activity(), ProjectDetailsView, ProjectDetailsRouter {


    companion object {
        val PARM_PROJECT_NAME = "__projectName"
    }

    var screenWidth: Int = 0

    var transitionDistance: Int = 0

    var actionsShowing = false

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
    }

    override fun onResume() {
        super.onResume()

        presenter.start()
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
        projectDescription.text = description
    }

    override fun showName(title: String) {
        findViewById<TextView>(R.id.projectName).text = title
    }

    override fun onReadyToUse() {
    }

    override fun showError(error: ApplicationError) {
        Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
    }

    override fun showTaskDetails(task: Task?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}