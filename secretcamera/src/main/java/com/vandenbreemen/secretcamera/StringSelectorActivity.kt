package com.vandenbreemen.secretcamera

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials

const val SELECTED_STRING = "__SELECTED_STRING"

class StringSelectorWorkflow() : Parcelable {

    constructor(activity: Class<out Activity>, items: ArrayList<String>) : this() {
        this.activityClassStr = activity.canonicalName
        this.items = items
    }

    constructor(activity: Class<out Activity>, items: ArrayList<String>, credentials: SFSCredentials) : this(activity, items) {
        this.credentials = credentials
    }

    var activityClassStr: String = ""

    lateinit var selected: String

    var credentials: SFSCredentials? = null

    lateinit var items: List<String>

    fun getActivityClass(): Class<in Activity> = Class.forName(activityClassStr) as Class<in Activity>

    constructor(parcel: Parcel) : this() {
        activityClassStr = parcel.readString()
        selected = parcel.readString()
        credentials = parcel.readParcelable(SFSCredentials::class.java.classLoader)
        items = parcel.createStringArrayList()
    }



    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(activityClassStr)
        parcel.writeString(selected)
        parcel.writeParcelable(credentials, flags)
        parcel.writeStringList(items)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StringSelectorWorkflow> {
        override fun createFromParcel(parcel: Parcel): StringSelectorWorkflow {
            return StringSelectorWorkflow(parcel)
        }

        override fun newArray(size: Int): Array<StringSelectorWorkflow?> {
            return arrayOfNulls(size)
        }
    }


}

class StringSelection(val selectedString: String) : Parcelable {

    var credentials: SFSCredentials? = null

    constructor(parcel: Parcel) : this(parcel.readString()) {
        credentials = parcel.readParcelable(SFSCredentials::class.java.classLoader)
    }

    constructor(selectedString: String, credentials: SFSCredentials) : this(selectedString) {
        this.credentials = credentials
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(selectedString)
        parcel.writeParcelable(credentials, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StringSelection> {
        override fun createFromParcel(parcel: Parcel): StringSelection {
            return StringSelection(parcel)
        }

        override fun newArray(size: Int): Array<StringSelection?> {
            return arrayOfNulls(size)
        }
    }


}

class StringSelectorActivity : Activity() {

    companion object {
        const val WORKFLOW = "__WORKFLOW"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_string_selector)

        val workflow = intent.getParcelableExtra<StringSelectorWorkflow>(WORKFLOW)!!
        val items = workflow.items

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items)
        val listView = findViewById<ListView>(R.id.itemList)
        listView.setAdapter(adapter)
        listView.onItemClickListener = AdapterView.OnItemClickListener({ parent, view, position, id ->

            val selected = adapter.getItem(position)
            val intent = Intent(this, workflow.getActivityClass())

            workflow.credentials?.let {
                intent.putExtra(SELECTED_STRING, StringSelection(selected, it.copy()))
                it.finalize()
            } ?: run {
                intent.putExtra(SELECTED_STRING, StringSelection(selected))
            }


            startActivity(intent)
        })
    }

    fun onOk(view: View) {

    }

    fun onCancel(view: View) {

    }
}
