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

const val SELECTED_STRING = "__SELECTED_STRING"

class StringSelectorWorkflow() : Parcelable {

    constructor(activity: Class<out Activity>) : this() {
        this.activityClassStr = activity.canonicalName
    }

    var activityClassStr: String = ""

    lateinit var selected: String

    constructor(parcel: Parcel) : this() {
        activityClassStr = parcel.readString()
        selected = parcel.readString()
    }

    fun getActivityClass(): Class<in Activity> = Class.forName(activityClassStr) as Class<in Activity>

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(activityClassStr)
        parcel.writeString(selected)
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
        const val LIST = "__ITEM_LIST"
        const val WORKFLOW = "__WORKFLOW"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_string_selector)

        val workflow = intent.getParcelableExtra<StringSelectorWorkflow>(WORKFLOW)!!
        val items = intent.getStringArrayListExtra(LIST)!!

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
