package com.vandenbreemen.secretcamera

import com.vandenbreemen.secretcamera.mvp.SFSMenuContract
import com.vandenbreemen.secretcamera.mvp.impl.SFSMainMenuPresenterImpl
import junit.framework.TestCase.assertTrue
import org.junit.Test

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
class SFSMenuContractTest {

    var takePicCalled:Boolean = false

    val mainMenuView: SFSMenuContract.SFSMainMenuView = object :SFSMenuContract.SFSMainMenuView{
        override fun gotoTakePicture() {
            takePicCalled = true
        }

    }

    val sut:SFSMenuContract.SFSMainMenuPresenter = SFSMainMenuPresenterImpl(mainMenuView)

    @Test
    fun shouldCallTakePicture(){
        sut.takePicture()
        assertTrue("Take picture", takePicCalled)
    }

}