package com.vandenbreemen.sfs_extendable

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vandenbreemen.mobilesecurestorage.android.fragment.SFSNavFragment
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.sfs_extendable.overview.SFSOverview

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SFSNavFragment.GET_CREDENTIALS_ACTION) {
            if (resultCode == RESULT_OK) {
                data?.getParcelableExtra<SFSCredentials>(SFSCredentials.PARM_CREDENTIALS)?.let { sfsCredentials ->
                    val goToSFSOverview = Intent(this, SFSOverview::class.java)
                    goToSFSOverview.putExtra(SFSCredentials.PARM_CREDENTIALS, sfsCredentials)
                    startActivity(goToSFSOverview)
                }

            } else {
                startActivity(intent)
            }
        }
    }
}
