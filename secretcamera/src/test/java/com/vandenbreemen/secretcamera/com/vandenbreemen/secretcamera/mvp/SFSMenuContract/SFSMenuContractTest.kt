package com.vandenbreemen.secretcamera.com.vandenbreemen.secretcamera.mvp.SFSMenuContract

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.secretcamera.mvp.SFSMenuContract
import com.vandenbreemen.secretcamera.mvp.impl.SFSMainMenuModel
import com.vandenbreemen.secretcamera.mvp.impl.SFSMainMenuPresenterImpl
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SFSMenuPresenterTest {

    @Mock
    lateinit var view: SFSMenuContract.SFSMainMenuView

    @Mock
    lateinit var model: SFSMainMenuModel

    @Mock
    lateinit var copyOfCredentials: SFSCredentials

    lateinit var presenter: SFSMenuContract.SFSMainMenuPresenter

    @Before
    fun setup() {
        this.presenter = SFSMainMenuPresenterImpl(model, view)
        `when`(model.copyCredentials()).thenReturn(copyOfCredentials)
    }

    @Test
    fun shouldStartGallery() {
        this.presenter.openGallery()
        verify(view).gotoGallery(copyOfCredentials)
    }

}