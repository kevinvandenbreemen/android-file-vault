package com.vandenbreemen.secretcamera

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import com.vandenbreemen.mobilesecurestorage.android.api.FileWorkflow
import com.vandenbreemen.mobilesecurestorage.android.fragment.SFSNavFragment
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.secretcamera.mvp.SFSMenuContract
import com.vandenbreemen.secretcamera.mvp.impl.SFSMainMenuPresenterImpl

class MainActivity : Activity(), SFSMenuContract.SFSMainMenuView {


    /**
     * File access workflow (containing the file we're going to be working with)
     */
    var fsWorkflow: FileWorkflow? = null

    var sfsCredentials:SFSCredentials? = null

    var mainMenuPresenter: SFSMenuContract.SFSMainMenuPresenter? = null

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

        if(fsWorkflow?.fileOrDirectory != null && intent.getParcelableExtra<SFSCredentials>(SFSCredentials.PARM_CREDENTIALS) != null){
            sfsCredentials = intent.getParcelableExtra<SFSCredentials>(SFSCredentials.PARM_CREDENTIALS)

            findViewById<ViewGroup>(R.id.mainSection).addView(
                layoutInflater.inflate(R.layout.main_screen_selections, findViewById(R.id.mainSection), false))
            mainMenuPresenter = SFSMainMenuPresenterImpl(this)

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

    override fun gotoTakeNote() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun gotoTakePicture() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
