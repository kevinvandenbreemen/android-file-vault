package com.vandenbreemen.secretcamera

import android.app.Activity
import android.os.Bundle
import com.vandenbreemen.mobilesecurestorage.android.api.FileWorkflow
import com.vandenbreemen.mobilesecurestorage.android.fragment.SFSNavFragment

class MainActivity : Activity() {

    /**
     * File access workflow (containing the file we're going to be working with)
     */
    var fsWorkflow: FileWorkflow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //  Get file workflow
        savedInstanceState?.let {
            fsWorkflow = it.getParcelable(FileWorkflow.PARM_WORKFLOW_NAME)
        } ?: run{
            fsWorkflow = intent.getParcelableExtra(FileWorkflow.PARM_WORKFLOW_NAME)
        }

        fsWorkflow = fsWorkflow?: FileWorkflow()

        if(fsWorkflow?.fileOrDirectory != null && fsWorkflow?.fileOrDirectory!!.isFile){

        }
        else{   //  Otherwise show the FS select fragment!
            val frag = SFSNavFragment()
            fsWorkflow!!.activityToStartAfterTargetActivityFinished = javaClass
            savedInstanceState?.let {
                frag.arguments = it
            } ?: run{
                frag.workflow = fsWorkflow!!
            }
            frag.setCancelAction(javaClass)
            fragmentManager.beginTransaction().add(R.id.upperSection, frag).commit()
        }


    }
}
