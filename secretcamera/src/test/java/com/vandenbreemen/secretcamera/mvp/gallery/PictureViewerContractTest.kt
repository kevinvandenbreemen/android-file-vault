package com.vandenbreemen.secretcamera.mvp.gallery

import android.graphics.Bitmap
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
@RunWith(RobolectricTestRunner::class)
class PictureViewerContractTest {

    @get:Rule
    val rule: MockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var model: PictureViewerModel

    @Mock
    lateinit var view: PictureViewerView

    @Mock
    lateinit var bitmap: Bitmap

    lateinit var presenter: PictureViewerPresenter

    @Before
    fun setup() {
        RxJavaPlugins.setComputationSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }

        this.presenter = PictureViewerPresenterImpl(model, view)
    }

    @Test
    fun shouldShowFirstAvailableImage() {
        `when`(model.currentFile()).thenReturn(Single.just("file"))
        `when`(model.loadImageForDisplay("file")).thenReturn(Single.just(bitmap))
        presenter.displayCurrentImage()
        verify(model).currentFile()
        verify(view).displayImage(bitmap)
    }

    @Test
    fun shouldHandleNoImagesAvailable() {
        `when`(model.currentFile()).thenReturn(Single.error(ApplicationError("No images available")))
        presenter.displayCurrentImage()
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
    fun shouldShowHideSpinnerWhenShowingNextImage() {
        `when`(model.loadImage("file2")).thenReturn(Single.just(bitmap))
        `when`(model.nextFile()).thenReturn(Single.just("file2"))
        presenter.nextImage()
        verify(view).showLoadingSpinner()
        verify(view).hideLoadingSpinner()
    }

    @Test
    fun shouldShowHideSpinnerWhenShowingPrevImage() {
        `when`(model.loadImage("file2")).thenReturn(Single.just(bitmap))
        `when`(model.prevFile()).thenReturn(Single.just("file2"))
        presenter.previousImage()
        verify(view).showLoadingSpinner()
        verify(view).hideLoadingSpinner()
    }

    @Test
    fun shouldShowSpinnerWhileLoadingImage() {
        `when`(model.loadImageForDisplay("file2")).thenReturn(Single.just(bitmap))
        presenter.selectImageToDisplay("file2")
        verify(view).showLoadingSpinner()
        verify(view).hideLoadingSpinner()
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