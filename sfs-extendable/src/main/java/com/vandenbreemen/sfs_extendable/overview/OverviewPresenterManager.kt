package com.vandenbreemen.sfs_extendable.overview

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestoragemvp.mgt.PresenterManager

/**
 *
 * @author kevin
 */
class OverviewPresenterManager : PresenterManager() {

    override fun buildPresenters(credentials: SFSCredentials) {
        super.addPresenter(OverviewPresenter(OverviewModel(credentials)))
    }
}