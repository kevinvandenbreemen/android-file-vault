package com.vandenbreemen.secretcamera

import android.app.Activity
import android.os.Bundle
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials

class TakeNoteActivity : Activity() {

    lateinit var credentials: SFSCredentials

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_note)

        this.credentials = intent.getParcelableExtra(SFSCredentials.PARM_CREDENTIALS) as SFSCredentials
    }


}
