package com.vandenbreemen.sfs_extendable.androidcomponents

import androidx.appcompat.app.AppCompatActivity
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

}