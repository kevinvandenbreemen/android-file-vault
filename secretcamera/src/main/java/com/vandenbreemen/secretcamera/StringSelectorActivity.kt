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

    var activityClassStr: String = ""

    lateinit var selected: String

    var credentials: SFSCredentials? = null

    lateinit var items: List<String>

    constructor(parcel: Parcel) : this() {
        activityClassStr = parcel.readString()
        selected = parcel.readString()
        credentials = parcel.readParcelable(SFSCredentials::class.java.classLoader)
        items = parcel.createStringArrayList()
    }

    fun getActivityClass(): Class<in Activity> = Class.forName(activityClassStr) as Class<in Activity>

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
        listView.setOnItemClickListener(AdapterView.OnItemClickListener({ parent, view, position, id ->
            val selected = adapter.getItem(position)
            val intent = Intent(this, workflow.getActivityClass())
            intent.putExtra(SELECTED_STRING, selected)
            startActivity(intent)
        }))
    }

    fun onOk(view: View) {

    }

    fun onCancel(view: View) {

    }
}
