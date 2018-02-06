package com.vandenbreemen.mobilesecurestorage.android

import android.app.Activity
import android.os.Bundle
import com.vandenbreemen.mobilesecurestorage.R

class FileSelectActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_select)
    }
}
