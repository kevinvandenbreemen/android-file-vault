package com.vandenbreemen.secretcamera.util

import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.vandenbreemen.secretcamera.R

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
class NoteTakingRobot {

    fun checkTitleDisplayed() {
        assertDisplayed(R.id.title)
    }

    fun checkContentDisplayed() {
        assertDisplayed(R.id.content)
    }

}