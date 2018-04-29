package com.vandenbreemen.secretcamera.mvp.gallery

import android.graphics.Bitmap
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import io.reactivex.Single
import junit.framework.TestCase.assertEquals
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
        `when`(model.loadImageForDisplay("file")).thenReturn(Single.just(bitmap))
        this.presenter = PictureViewerPresenterImpl(model, view)
    }

    @Test
    fun shouldShowFirstAvailableImage() {
        presenter.selectImageToDisplay()
        verify(model).currentFile()
        verify(view).displayImage(bitmap)
    }

    @Test
    fun shouldHandleNoImagesAvailable() {
        `when`(model.currentFile()).thenReturn(Single.error(ApplicationError("No images available")))
        presenter.selectImageToDisplay()
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
        `when`(model.loadImageForDisplay("file2")).thenReturn(Single.just(bitmap))
        presenter.selectImageToDisplay("file2")
        verify(view).displayImage(bitmap)
    }

    @Test
    fun shouldHideImageSelectorWhenSelectingImageToDisplay() {
        `when`(model.loadImageForDisplay("file2")).thenReturn(Single.just(bitmap))
        presenter.selectImageToDisplay("file2")
        verify(view).hideImageSelector()
    }

    @Test
    fun shouldRetrieveCurrentSelectedImage() {
        `when`(model.currentFile()).thenReturn(Single.just("currentFile"))
        assertEquals("currentFile", presenter.currentImageFileName().blockingGet())
    }

}