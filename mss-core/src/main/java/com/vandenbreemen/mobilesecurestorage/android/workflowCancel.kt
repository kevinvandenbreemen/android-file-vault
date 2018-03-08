package com.vandenbreemen.mobilesecurestorage.android

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.vandenbreemen.mobilesecurestorage.android.api.FileWorkflow

/**
 * Handle cancel action
 */
fun handleWorkflowCancel(activity: Activity, workflow: FileWorkflow?) {
    workflow?.cancelActivity?.let {
        val cancelIntent = Intent(activity, workflow?.cancelActivity!!)
        activity.startActivity(cancelIntent)
        activity.finish()
    } ?: run {
        Log.w("WorkflowCancelStandard", "No cancel action defined")
    }
}