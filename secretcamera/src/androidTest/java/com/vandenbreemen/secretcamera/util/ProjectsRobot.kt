package com.vandenbreemen.secretcamera.util

import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.vandenbreemen.secretcamera.R

class ProjectsRobot {

    fun checkOnProjectsPage() {
        assertDisplayed(R.id.addProjectFab)
    }

}
