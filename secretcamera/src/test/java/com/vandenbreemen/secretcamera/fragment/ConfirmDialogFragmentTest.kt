package com.vandenbreemen.secretcamera.fragment

import android.widget.Button
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vandenbreemen.secretcamera.R
import junit.framework.TestCase.*
import kotlinx.android.synthetic.main.layout_delete_confirm.view.*
import kotlinx.android.synthetic.main.layout_kds_info_item.view.*
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

    @Test
    fun `Displays list of Files that will Be Deleted`() {
        var validated = false
        val factory = ConfirmDialogFragmentFactory(fileNames = listOf("A", "B"), callBack = {

        })
        launchFragmentInContainer<ConfirmDeleteDialogFragment>(null, factory = factory).onFragment { fragment ->
            fragment.view?.apply {
                assertEquals(2, itemList.childCount)

                assertNotNull(itemList.getChildAt(0).textContent)
                assertEquals("A", itemList.getChildAt(0).textContent.text)
                assertEquals("B", itemList.getChildAt(1).textContent.text)

                validated = true
            }
        }

        assertTrue(validated)
    }

}