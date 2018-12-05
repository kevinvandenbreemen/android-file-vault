package com.vandenbreemen.secretcamera

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils

/**
 *
 * @author kevin
 */
class ProjectsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
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

}