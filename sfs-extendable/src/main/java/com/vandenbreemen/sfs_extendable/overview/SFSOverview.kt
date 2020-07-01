package com.vandenbreemen.sfs_extendable.overview

import android.os.Bundle
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestoragemvp.mgt.PresenterManager
import com.vandenbreemen.sfs_extendable.R
import com.vandenbreemen.sfs_extendable.androidcomponents.SFSActivity
import kotlinx.android.synthetic.main.activity_s_f_s_overview.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SFSOverview : SFSActivity(), OverviewView {

    override fun buildPresenterManager(): PresenterManager {
        return OverviewPresenterManager()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_s_f_s_overview)

        setSupportActionBar(toolbar)
    }

    override fun onResume() {
        super.onResume()

        super.buildPresenters()

        val presenter = presenterManager.getPresenter<OverviewPresenter>()
        presenter.setView(this)

        CoroutineScope(Dispatchers.Default).launch {
            presenter.start()
        }

    }

    override fun showFilesCount(count: Int) {
        filesCount.text = count.toString()
    }

    override fun showError(error: ApplicationError) {

    }
}