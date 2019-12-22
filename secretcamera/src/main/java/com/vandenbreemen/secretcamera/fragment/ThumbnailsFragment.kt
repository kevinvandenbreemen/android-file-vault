package com.vandenbreemen.secretcamera.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vandenbreemen.secretcamera.R
import com.vandenbreemen.secretcamera.ThumbnailAdapter
import com.vandenbreemen.secretcamera.mvp.gallery.PictureViewerPresenter

/**
 *
 * @author kevin
 */
class ThumbnailsFragment(private val files: List<String>, private val currentImageFileName: String,
                         private val presenter: PictureViewerPresenter) : DialogFragment() {

    init {
        setStyle(STYLE_NO_FRAME, 0)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.layout_gallery_selector, container, false)
        if (view == null) {
            return view
        }

        val recycler = view.findViewById<RecyclerView>(R.id.imageRecyclerView)
        val adapter = ThumbnailAdapter(files, presenter)
        val layoutManager = LinearLayoutManager(context)
        recycler.layoutManager = layoutManager
        recycler.adapter = adapter
        recycler.layoutManager?.scrollToPosition(files.indexOf(currentImageFileName))
        adapter.notifyDataSetChanged()

        return view
    }

}