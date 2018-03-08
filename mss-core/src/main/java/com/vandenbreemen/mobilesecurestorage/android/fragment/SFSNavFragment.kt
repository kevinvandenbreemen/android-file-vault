package com.vandenbreemen.mobilesecurestorage.android.fragment

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.vandenbreemen.mobilesecurestorage.R
import com.vandenbreemen.mobilesecurestorage.android.CreateSecureFileSystem
import com.vandenbreemen.mobilesecurestorage.android.FileSelectActivity
import com.vandenbreemen.mobilesecurestorage.android.LoadSecureFileSystem
import com.vandenbreemen.mobilesecurestorage.android.api.FileWorkflow


/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
class SFSNavFragment(): Fragment() {

    /**
     * Workflow for file access
     */
    lateinit var workflow: FileWorkflow

    override fun setArguments(args: Bundle?) {
        this.workflow = args?.getParcelable(FileWorkflow.PARM_WORKFLOW_NAME)?: FileWorkflow()
    }

    fun setCancelAction(clz: Class<Activity>){
        this.workflow.cancelActivity = clz
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val viewGroup: ViewGroup = inflater?.inflate(R.layout.layout_sfs_nav, container,  false) as ViewGroup

        var button = viewGroup.findViewById<Button>(R.id.createNew)
        button.setOnClickListener {
            val intent: Intent = Intent(activity, FileSelectActivity::class.java)
            this.workflow.targetActivity = CreateSecureFileSystem::class.java
            intent.putExtra(FileWorkflow.PARM_WORKFLOW_NAME, this.workflow)
            intent.putExtra(FileSelectActivity.PARM_DIR_ONLY, true)
            activity.startActivity(intent)
        }

        button = viewGroup.findViewById(R.id.loadExisting)
        button.setOnClickListener {
            val intent: Intent = Intent(activity, FileSelectActivity::class.java)
            this.workflow.targetActivity = LoadSecureFileSystem::class.java
            intent.putExtra(FileWorkflow.PARM_WORKFLOW_NAME, this.workflow)
            activity.startActivity(intent)
        }


        return viewGroup
    }

}