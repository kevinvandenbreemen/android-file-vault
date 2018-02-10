package com.vandenbreemen.mobilesecurestorage.android

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.vandenbreemen.mobilesecurestorage.R
import com.vandenbreemen.mobilesecurestorage.android.mvp.FileSelectController
import com.vandenbreemen.mobilesecurestorage.android.mvp.FileSelectModel
import com.vandenbreemen.mobilesecurestorage.android.mvp.FileSelectView
import java.io.File

class FileSelectActivity : Activity(), FileSelectView, ActivityCompat.OnRequestPermissionsResultCallback {

    companion object {
        /**
         * Permissions for file IO
         */
        const val PERM_REQUEST_ID = 2501
    }

    /**
     * App controller
     */
    private lateinit var controller: FileSelectController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_select)

        //  Check for file IO permissions
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
             PERM_REQUEST_ID)
        }

        val model = FileSelectModel(this)
        controller = FileSelectController(model, this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray){
        when(requestCode){
            PERM_REQUEST_ID->{
                if((grantResults!!.isNotEmpty() && grantResults!![0] == PackageManager.PERMISSION_GRANTED)){
                    controller.start()
                }
            }
        }
    }

    override fun listFiles(files: MutableList<File>?) {
        val listView = findViewById<ListView>(R.id.fileList);

        val adapter = object : ArrayAdapter<File>(this, R.layout.layout_file_item) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

                val group = layoutInflater.inflate(R.layout.layout_file_item, parent, false)
                group.findViewById<TextView>(R.id.itemLabel).text = (files!![position].absolutePath)

                return super.getView(position, convertView, parent)
            }
        }
        adapter.addAll(files)

        listView.adapter = adapter

    }
}
