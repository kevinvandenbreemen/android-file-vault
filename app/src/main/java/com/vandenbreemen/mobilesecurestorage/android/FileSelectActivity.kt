package com.vandenbreemen.mobilesecurestorage.android

import android.app.Activity
import android.os.Bundle
import com.vandenbreemen.mobilesecurestorage.R
import com.vandenbreemen.mobilesecurestorage.android.mvp.FileSelectView

class FileSelectActivity : Activity(), FileSelectView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_select)
    }
}
