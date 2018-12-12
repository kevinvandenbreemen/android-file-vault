package com.vandenbreemen.secretcamera

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager

class ProjectDetailsActivity: Activity() {

    companion object {
        val PARM_PROJECT_NAME = "__projectName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        //setContentView(R.layout.activity_project_detail)
    }

}