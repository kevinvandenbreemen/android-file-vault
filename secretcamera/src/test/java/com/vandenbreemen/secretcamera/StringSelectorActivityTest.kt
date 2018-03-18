package com.vandenbreemen.secretcamera

import android.content.Intent
import android.widget.ListView
import org.hamcrest.CoreMatchers.`is`
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ErrorCollector
import org.junit.runner.RunWith
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowApplication
import java.util.*

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
@RunWith(RobolectricTestRunner::class)
class StringSelectorActivityTest {

    @get:Rule
    val errorCollector: ErrorCollector = ErrorCollector()

    @Test
    fun shouldDisplaySelections() {
        val intent = Intent(ShadowApplication.getInstance().applicationContext, StringSelectorActivity::class.java)
        intent.putStringArrayListExtra(StringSelectorActivity.LIST,
                ArrayList<String>(Arrays.asList("Larry", "Curly", "Moe")))

        val activity = buildActivity(StringSelectorActivity::class.java, intent)
                .create()
                .get()

        val listView = activity.findViewById<ListView>(R.id.itemList)
        val shadow = shadowOf(listView)
        shadow.populateItems()

        errorCollector.checkThat(listView.adapter.count, `is`(3))
        errorCollector.checkThat(listView.adapter.getItem(0) as String, `is`("Larry"))
        errorCollector.checkThat(listView.adapter.getItem(1) as String, `is`("Curly"))
        errorCollector.checkThat(listView.adapter.getItem(2) as String, `is`("Moe"))

    }

}