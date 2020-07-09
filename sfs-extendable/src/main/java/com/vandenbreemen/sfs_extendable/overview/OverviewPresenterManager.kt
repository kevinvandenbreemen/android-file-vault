package com.vandenbreemen.sfs_extendable.overview

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestoragemvp.mgt.PresenterManager
import com.vandenbreemen.mobilesecurestoragemvp.wrapper.StorageRepositoryProvider

/**
 *
 * @author kevin
 */
class OverviewPresenterManager : PresenterManager() {

    override fun buildPresenters(credentials: SFSCredentials, provider: StorageRepositoryProvider) {
        super.addPresenter(OverviewPresenter(OverviewModel(credentials, provider)))
    }
}