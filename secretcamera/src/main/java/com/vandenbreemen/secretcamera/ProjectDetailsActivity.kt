package com.vandenbreemen.secretcamera

import android.animation.Animator
import android.app.Activity
import android.graphics.Point
import android.os.Bundle
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_project_detail.*

class ProjectDetailsActivity: Activity() {

    companion object {
        val PARM_PROJECT_NAME = "__projectName"
    }

    var screenWidth: Int = 0

    var actionsShowing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_project_detail)

        //  Work out the size of the screen
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        screenWidth = size.x

        //  Move the actions off to the left

        actionsButton.translationX = -1 * (screenWidth.toFloat() - 50)
        descriptionCard.translationX = -1 * (screenWidth.toFloat() - 50)

        actionsButton.setOnClickListener { v ->
            if (actionsShowing) {
                hideActionsSection()
            } else {
                showActionsSection()
            }
        }
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
                .translationXBy(screenWidth.toFloat())
                .setListener(animationListener).duration = 300

        descriptionCard.animate()
                .translationXBy(screenWidth.toFloat())
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
                .translationXBy(-screenWidth.toFloat())
                .setListener(animationListener).duration = 300

        descriptionCard.animate()
                .translationXBy(-screenWidth.toFloat())
                .setListener(animationListener).duration = 300
    }

}