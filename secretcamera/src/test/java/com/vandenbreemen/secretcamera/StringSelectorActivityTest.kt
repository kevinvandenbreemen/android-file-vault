package com.vandenbreemen.secretcamera

import android.content.Intent
import android.widget.ListView
import com.vandenbreemen.mobilesecurestorage.android.FileSelectActivity
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.secretcamera.StringSelectorActivity.Companion.WORKFLOW
import junit.framework.TestCase.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers.notNullValue
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
        val arrayList = ArrayList<String>(Arrays.asList("Larry", "Curly", "Moe"))
        intent.putExtra(WORKFLOW, StringSelectorWorkflow(StringSelectorActivity::class.java, arrayList))

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
        val arrayList = ArrayList<String>(Arrays.asList("Larry", "Curly", "Moe"))
        intent.putExtra(StringSelectorActivity.WORKFLOW, StringSelectorWorkflow(FileSelectActivity::class.java, arrayList))

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
        errorCollector.checkThat(nextActivity.getParcelableExtra(SELECTED_STRING), notNullValue())

        val stringSelection: StringSelection = nextActivity.getParcelableExtra(SELECTED_STRING)
        errorCollector.checkThat(stringSelection.selectedString, `is`("Curly"))

    }

    @Test
    fun shouldCarrySFSCredentials() {

        val testFile = createTempFile("test")

        val credentials = SFSCredentials(testFile, SecureString.fromPassword("teest"))

        val intent = Intent(ShadowApplication.getInstance().applicationContext, StringSelectorActivity::class.java)
        val arrayList = ArrayList<String>(Arrays.asList("Larry", "Curly", "Moe"))
        intent.putExtra(StringSelectorActivity.WORKFLOW, StringSelectorWorkflow(FileSelectActivity::class.java, arrayList, credentials))

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

        val stringSelection: StringSelection = nextActivity.getParcelableExtra(SELECTED_STRING)
        errorCollector.checkThat(stringSelection.selectedString, `is`("Curly"))
        errorCollector.checkThat(stringSelection.credentials, notNullValue())
        assertTrue("Password persisted", SecureString.fromPassword("teest").equals(stringSelection.credentials!!.password))
    }

}