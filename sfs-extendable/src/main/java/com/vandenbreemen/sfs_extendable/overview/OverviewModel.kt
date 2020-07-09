package com.vandenbreemen.sfs_extendable.overview

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestoragemvp.Model
import com.vandenbreemen.mobilesecurestoragemvp.wrapper.StorageRepositoryProvider

/**
 *
 * @author kevin
 */
class OverviewModel(credentials: SFSCredentials, provider: StorageRepositoryProvider) : Model(credentials, provider) {

    fun getFilesCount(): Int {
        return storage.lsc()
    }

    override fun onClose() {

    }

    override fun setup() {

    }
}