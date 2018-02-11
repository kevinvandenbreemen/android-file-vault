package com.vandenbreemen.mobilesecurestorage.android.mvp.loadfilesystem

import com.vandenbreemen.mobilesecurestorage.android.api.ErrorDisplay
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials

/**
 * <h2>Intro</h2>
 * Load file system
 * <h2>Other Details</h2>
 * @author kevin
 */
interface LoadFileSystemView : ErrorDisplay {

    /**
     * Called when loading succeeded
     */
    fun onLoadSuccess(credentials: SFSCredentials)

}