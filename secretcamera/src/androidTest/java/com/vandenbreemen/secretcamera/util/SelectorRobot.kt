package com.vandenbreemen.secretcamera.util

import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.matcher.ViewMatchers.withId
import com.vandenbreemen.secretcamera.R
import org.hamcrest.CoreMatchers.anything

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
class SelectorRobot {

    fun selectItem(index: Int) {
        onData(anything()).inAdapterView(withId(R.id.itemList)).atPosition(index).perform(click())
    }

}