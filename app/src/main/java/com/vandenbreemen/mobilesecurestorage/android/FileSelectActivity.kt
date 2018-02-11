package com.vandenbreemen.mobilesecurestorage.android

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.vandenbreemen.mobilesecurestorage.R
import com.vandenbreemen.mobilesecurestorage.android.api.FileWorkflow
import com.vandenbreemen.mobilesecurestorage.android.mvp.fileselect.FileSelectController
import com.vandenbreemen.mobilesecurestorage.android.mvp.fileselect.FileSelectModel
import com.vandenbreemen.mobilesecurestorage.android.mvp.fileselect.FileSelectView
import java.io.File

class FileSelectActivity : Activity(), FileSelectView, ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * Callback - usually for testing only
     */
    interface FileSelectListener {
        fun onFileSelect(file: File)
    }

    companion object {
        /**
         * Permissions for file IO
         */
        const val PERM_REQUEST_ID = 2501

        /**
         * Indicates that we're selecting a directory only
         */
        const val PARM_DIR_ONLY = "DIR_ONLY"
    }

    /**
     * App controller
     */
    private lateinit var controller: FileSelectController

    private var listener: FileSelectListener = object : FileSelectListener {
        override fun onFileSelect(file: File) {
            onSelectedFile(file)
        }

    }

    private var workflow: FileWorkflow = FileWorkflow()  //  Default value for null safety...

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_select)

        val model = FileSelectModel(this)
        model.setSelectDirectories(intent.getBooleanExtra(PARM_DIR_ONLY, false))

        controller = FileSelectController(model, this)

        this.workflow =
                if (intent.getParcelableExtra(FileWorkflow.PARM_WORKFLOW_NAME) as? FileWorkflow != null)
                    intent.getParcelableExtra(FileWorkflow.PARM_WORKFLOW_NAME) as FileWorkflow
                else FileWorkflow()
    }

    /**
     * Set listener for testing
     */
    protected fun setListener(listener: FileSelectListener) {
        this.listener = listener;
    }

    override fun onResume() {

        super.onResume()

        //  Check for file IO permissions
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERM_REQUEST_ID)
        } else {
            controller.start()
        }
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

    override fun select(selected: File) {
        listener.onFileSelect(selected)
    }

    private fun onSelectedFile(selected: File) {
        if (workflow.targetActivity != null) {
            val intent = Intent(this, workflow.targetActivity)
            intent.putExtra(FileWorkflow.PARM_WORKFLOW_NAME, workflow)
            startActivity(intent)
            finish()
        } else {
            Log.w("NextStepInFileSelect", "No target activity found!  Did you forget to include a ${FileWorkflow::class.java.simpleName} in your intent?")
        }
    }

    override fun listFiles(files: MutableList<File>?) {
        val listView = findViewById<ListView>(R.id.fileList);

        val adapter = object : ArrayAdapter<File>(this, android.R.layout.simple_list_item_1) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

                val group = layoutInflater.inflate(R.layout.layout_file_item, parent, false)
                group.findViewById<TextView>(R.id.itemLabel).text = (files!![position].absolutePath)

                return super.getView(position, convertView, parent)
            }
        }

        listView.setOnItemClickListener { parent, view, position, id ->
            val selected = adapter.getItem(position)
            controller.select(selected)
        }

        adapter.addAll(files)

        listView.adapter = adapter

    }

    fun onOkay(view: View?) {
        controller.confirm()
    }
}