package com.vandenbreemen.secretcamera.util

import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.vandenbreemen.secretcamera.R


class GalleryRobot {

    fun checkOnGalleryScreen() {

        assertDisplayed(R.id.galleryTitle)
    }

    fun clickImportImages() {
        assertDisplayed(R.id.importImages)
        clickOn(R.id.importImages)
    }

    fun checkOnDirectorySelectScreen() {
        assertDisplayed(R.id.fileList)
    }

    fun selectDirectory(dirName: String) {
        clickOn(dirName)
        clickOn(R.id.ok)
    }

    fun checkOnImportScreen() {
        assertDisplayed(R.string.importing)
    }
}