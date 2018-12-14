package com.vandenbreemen.secretcamera

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
    }

}