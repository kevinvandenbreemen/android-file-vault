package com.vandenbreemen.secretcamera.mvp.gallery

import android.graphics.Bitmap
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
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
        `when`(model.currentFile()).thenReturn(Single.just("file"))
        `when`(model.loadImage("file")).thenReturn(Single.just(bitmap))
        this.presenter = PictureViewerPresenterImpl(model, view)
    }

    @Test
    fun shouldShowFirstAvailableImage() {
        presenter.displayImage()
        verify(model).currentFile()
        verify(view).displayImage(bitmap)
    }

    @Test
    fun shouldHandleNoImagesAvailable() {
        `when`(model.currentFile()).thenReturn(Single.error(ApplicationError("No images available")))
        presenter.displayImage()
        verify(view, never()).displayImage(bitmap)
    }

    @Test
    fun shouldShowImageSelectionsWhenSelectImageCalled() {
        val filesList = listOf("file1", "file2")
        `when`(model.listImages()).thenReturn(Single.just(filesList))
        presenter.showSelector()
        verify(view).showImageSelector(filesList)
    }

    @Test
    fun shouldShowSelectedImage() {
        `when`(model.loadImage("file")).then { throw RuntimeException("Incorrect filename") }
        `when`(model.loadImage("file2")).thenReturn(Single.just(bitmap))
        presenter.displayImage("file2")
        verify(view).displayImage(bitmap)
    }

}