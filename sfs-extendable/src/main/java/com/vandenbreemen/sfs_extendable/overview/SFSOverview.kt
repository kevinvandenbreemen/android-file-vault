package com.vandenbreemen.sfs_extendable.overview

import android.os.Bundle
import com.vandenbreemen.mobilesecurestoragemvp.mgt.PresenterManager
import com.vandenbreemen.sfs_extendable.R
import com.vandenbreemen.sfs_extendable.androidcomponents.SFSActivity

class SFSOverview : SFSActivity() {

    override fun buildPresenterManager(): PresenterManager {
        return OverviewPresenterManager()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_s_f_s_overview)
    }
}