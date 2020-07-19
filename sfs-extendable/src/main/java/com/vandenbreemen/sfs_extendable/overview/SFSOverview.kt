package com.vandenbreemen.sfs_extendable.overview

import android.os.Bundle
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestoragemvp.mgt.PresenterManager
import com.vandenbreemen.sfs_extendable.androidcomponents.SFSActivity
import com.vandenbreemen.sfs_extendable.databinding.ActivitySFSOverviewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SFSOverview : SFSActivity(), OverviewView {

    private lateinit var viewBinding: ActivitySFSOverviewBinding

    override fun buildPresenterManager(): PresenterManager {
        return OverviewPresenterManager()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivitySFSOverviewBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        setSupportActionBar(viewBinding.toolbar)


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
        viewBinding.filesCount.text = count.toString()
    }

    override fun showError(error: ApplicationError) {

    }
}