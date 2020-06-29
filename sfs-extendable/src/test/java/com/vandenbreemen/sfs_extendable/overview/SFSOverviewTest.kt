package com.vandenbreemen.sfs_extendable.overview

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vandenbreemen.sfs_extendable.app.SFExtentableApp
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author kevin
 */
@RunWith(AndroidJUnit4::class)
class SFSOverviewTest {

    @Test
    fun `Launch Activity`() {

        val intent = Intent(ApplicationProvider.getApplicationContext<SFExtentableApp>(), SFSOverview::class.java)

        val scenario = launchActivity<SFSOverview>(intent)
        scenario.moveToState(Lifecycle.State.CREATED)

    }

}