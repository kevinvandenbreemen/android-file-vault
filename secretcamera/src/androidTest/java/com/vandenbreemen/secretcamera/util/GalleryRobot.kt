package com.vandenbreemen.secretcamera.util

import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.vandenbreemen.secretcamera.R


class GalleryRobot {

    fun checkOnGalleryScreen() {
        assertDisplayed(R.id.galleryTitle)
    }

}