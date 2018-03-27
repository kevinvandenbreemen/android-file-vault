package com.vandenbreemen.secretcamera.util

import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo
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

    fun setTitle(title: String) {
        writeTo(R.id.title, title)
    }

    fun setContent(content: String) {
        writeTo(R.id.content, content)
    }

    fun clickOK() {
        clickOn(R.id.ok)
    }

    fun checkTitleIs(title: String) {
        assertContains(R.id.title, title)
    }

    fun checkContentIs(content: String) {
        assertContains(R.id.content, content)
    }

    fun clickEdit() {
        clickOn(R.id.edit)
    }

}