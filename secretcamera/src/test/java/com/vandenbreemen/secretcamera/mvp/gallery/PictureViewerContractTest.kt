package com.vandenbreemen.secretcamera.mvp.gallery

import android.graphics.Bitmap
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
@RunWith(MockitoJUnitRunner::class)
class PictureViewerContractTest {

    @Mock
    lateinit var model: PictureViewerModel

    @Mock
    lateinit var view: PictureViewerView

    @Mock
    lateinit var bitmap: Bitmap

    lateinit var presenter: PictureViewerPresenter

    @Before
    fun setup() {
        `when`(model.listImages()).thenReturn(Single.just(listOf("file")))
        `when`(model.loadImage("file")).thenReturn(Single.just(bitmap))
        this.presenter = PictureViewerPresenterImpl(model, view)
    }

    @Test
    fun shouldShowFirstAvailableImage() {
        presenter.displayImage()
        verify(model).listImages()
        verify(view).displayImage(bitmap)
    }

}