package com.vandenbreemen.secretcamera

import android.content.Intent
import android.widget.ListView
import com.vandenbreemen.mobilesecurestorage.android.FileSelectActivity
import com.vandenbreemen.secretcamera.StringSelectorActivity.Companion.WORKFLOW
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
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
        intent.putExtra(WORKFLOW, StringSelectorWorkflow())

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

    @Test
    fun shouldKickOffActivityWithSelection() {
        val intent = Intent(ShadowApplication.getInstance().applicationContext, StringSelectorActivity::class.java)
        intent.putStringArrayListExtra(StringSelectorActivity.LIST,
                ArrayList<String>(Arrays.asList("Larry", "Curly", "Moe")))
        intent.putExtra(StringSelectorActivity.WORKFLOW, StringSelectorWorkflow(FileSelectActivity::class.java))

        val activity = buildActivity(StringSelectorActivity::class.java, intent)
                .create()
                .get()

        val listView = activity.findViewById<ListView>(R.id.itemList)
        val shadow = shadowOf(listView)
        shadow.populateItems()

        shadow.performItemClick(1)

        val shadowAct = shadowOf(activity)
        val nextActivity = shadowAct.nextStartedActivity

        assertNotNull("Intent started", nextActivity)

        val nextShadow = shadowOf(nextActivity)
        assertEquals("Next activity", FileSelectActivity::class.java, nextShadow.intentClass)
        errorCollector.checkThat(nextActivity.getStringExtra(SELECTED_STRING), `is`("Curly"))

    }

}