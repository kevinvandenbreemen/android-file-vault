package com.vandenbreemen.secretcamera

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView

class StringSelectorActivity : Activity() {

    companion object {
        const val LIST = "__ITEM_LIST"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_string_selector)

        val items = intent.getStringArrayListExtra(LIST)
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items)
        findViewById<ListView>(R.id.itemList).setAdapter(adapter)
    }

    fun onOk(view: View) {

    }

    fun onCancel(view: View) {

    }
}
