package com.vandenbreemen.secretcamera.fragment

import android.widget.Button
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vandenbreemen.secretcamera.R
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 *
 * @author kevin
 */
@RunWith(AndroidJUnit4::class)
class ConfirmDialogFragmentTest {

    @Test
    fun `Clicking OK on the dialog calls the callback code`() {

        var callBackCalled: Boolean = false
        val factory = ConfirmDialogFragmentFactory(fileNames = listOf("A", "B"), callBack = {
            callBackCalled = true
        })
        launchFragmentInContainer<ConfirmDeleteDialogFragment>(null, factory = factory).onFragment { fragment ->
            fragment.view!!.findViewById<Button>(R.id.ok_button).callOnClick()
        }

        assertTrue(callBackCalled)
    }

    @Test
    fun `Dismiss The Confirmation Dialog does not call the callback logic`() {
        var callBackCalled: Boolean = false
        val factory = ConfirmDialogFragmentFactory(fileNames = listOf("A", "B"), callBack = {
            callBackCalled = true
        })
        launchFragmentInContainer<ConfirmDeleteDialogFragment>(null, factory = factory).onFragment { fragment ->
            fragment.view!!.findViewById<Button>(R.id.cancel_button).callOnClick()
        }

        assertFalse(callBackCalled)
    }

}