package com.vandenbreemen.secretcamera.mvp.gallery

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.secretcamera.mvp.impl.gallery.GalleryModel
import com.vandenbreemen.secretcamera.mvp.impl.gallery.GalleryPresenterImpl
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GalleryContractTest {

    lateinit var galleryPresenter: GalleryPresenter

    @Mock
    lateinit var galleryView: GalleryView

    @Mock
    lateinit var sfsCopy: SFSCredentials

    @Mock
    lateinit var galleryModel: GalleryModel

    @Before
    fun setup() {
        `when`(galleryModel.copyCredentials()).thenReturn(sfsCopy)
        this.galleryPresenter = GalleryPresenterImpl(galleryModel, galleryView)
    }

    @Test
    fun shouldStartDirectorySelect() {
        galleryPresenter.importDirectory()
        verify(galleryView).loadDirectoryImport(sfsCopy)
    }

}