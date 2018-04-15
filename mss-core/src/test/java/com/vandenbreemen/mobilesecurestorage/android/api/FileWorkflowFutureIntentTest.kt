package com.vandenbreemen.mobilesecurestorage.android.api

import android.content.Intent
import android.os.Parcel
import com.vandenbreemen.mobilesecurestorage.MainActivity
import junit.framework.TestCase.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

class TestValueProvider {

}

class TestFutureIntent : FutureIntent<TestValueProvider> {
    override fun populateIntentWithDetailsAboutFutureActivity(intent: Intent, provider: TestValueProvider) {

    }

    override fun populateIntentToStartFutureActivity(intentToStartFutureActivity: Intent, intentForCurrentActivity: Intent) {

    }

}

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
@RunWith(RobolectricTestRunner::class)
class FileWorkflowFutureIntentTest {

    @Test
    fun shouldPersistTypeOfFutureIntent() {
        var workflow = FileWorkflow()
        workflow.targetActivity = MainActivity::class.java
        workflow.setTargetActivityFutureIntent(TestFutureIntent::class.java)


        val parcel = Parcel.obtain()

        workflow.writeToParcel(parcel, workflow.describeContents())
        parcel.setDataPosition(0)

        val fromParcel = FileWorkflow.CREATOR.createFromParcel(parcel)

        val targetFutureIntent: FutureIntent<*>? = workflow.getTargetFutureIntent()
        assertNotNull("Future intent", targetFutureIntent)
    }

}