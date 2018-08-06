package com.vandenbreemen.mobilesecurestorage.android.fragment

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.vandenbreemen.mobilesecurestorage.R
import com.vandenbreemen.mobilesecurestorage.android.CreateSecureFileSystem
import com.vandenbreemen.mobilesecurestorage.android.LoadSecureFileSystem
import com.vandenbreemen.mobilesecurestorage.android.api.FileWorkflow


/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
class SFSNavFragment(): Fragment() {

    companion object {
        const val GET_CREDENTIALS_ACTION = 42
    }

    /**
     * Workflow for file access
     */
    var workflow: FileWorkflow = FileWorkflow()

    override fun setArguments(args: Bundle?) {
        this.workflow = args?.getParcelable(FileWorkflow.PARM_WORKFLOW_NAME)?: FileWorkflow()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val viewGroup: ViewGroup = inflater?.inflate(R.layout.layout_sfs_nav, container,  false) as ViewGroup

        var button = viewGroup.findViewById<Button>(R.id.createNew)
        button.setOnClickListener {
            val intent: Intent = Intent(activity, CreateSecureFileSystem::class.java)
            activity.startActivityForResult(intent, GET_CREDENTIALS_ACTION)
        }

        button = viewGroup.findViewById(R.id.loadExisting)
        button.setOnClickListener {
            val intent = Intent(activity, LoadSecureFileSystem::class.java)
            activity.startActivityForResult(intent, GET_CREDENTIALS_ACTION)
        }


        return viewGroup
    }

}