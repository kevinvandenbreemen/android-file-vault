package com.vandenbreemen.sfs_extendable.androidcomponents

import androidx.appcompat.app.AppCompatActivity
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestoragemvp.mgt.PresenterManager

/**
 *
 * @author kevin
 */
abstract class SFSActivity : AppCompatActivity() {

    /**
     * Management for creating presenters
     */
    protected val presenterManager: PresenterManager by lazy {
        buildPresenterManager()
    }

    protected abstract fun buildPresenterManager(): PresenterManager

    /**
     * Builds the presenter using the intent
     */
    protected fun buildPresenters() {
        intent.getParcelableExtra<SFSCredentials>(SFSCredentials.PARM_CREDENTIALS)?.let { credentials ->
            presenterManager.buildPresenters(credentials)
        }
    }

}