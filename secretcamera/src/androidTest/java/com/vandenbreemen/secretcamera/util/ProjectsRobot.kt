package com.vandenbreemen.secretcamera.util

import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.vandenbreemen.secretcamera.R

class ProjectsRobot {

    fun clickAddProject() {
        clickOn(R.id.addProjectFab)
        assertDisplayed(R.id.addProjectDialog)
    }

}
