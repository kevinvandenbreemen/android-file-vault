package com.vandenbreemen.secretcamera.mvp.gallery

import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

var imageFilesInteractorClosed = false

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
@Implements(ImageFilesInteractor::class)
class ShadowImageFilesInteractor {

    @Implementation
    fun close() {
        imageFilesInteractorClosed = true
    }

}