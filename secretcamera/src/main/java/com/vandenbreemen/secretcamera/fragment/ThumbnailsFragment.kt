package com.vandenbreemen.secretcamera.fragment

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vandenbreemen.secretcamera.R
import com.vandenbreemen.secretcamera.ThumbnailAdapter
import com.vandenbreemen.secretcamera.mvp.gallery.PictureViewerPresenter
import kotlinx.android.synthetic.main.layout_gallery_selector.view.*

/**
 *
 * @author kevin
 */
class ThumbnailsFragment(private val files: List<String>, private val currentImageFileName: String,
                         private val presenter: PictureViewerPresenter) : DialogFragment() {

    var listener: ThumbnailScreenListener? = null

    interface ThumbnailScreenListener {
        fun onCancel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.layout_gallery_selector, container, false)
        if (view == null) {
            return view
        }

        val recycler = view.findViewById<RecyclerView>(R.id.imageRecyclerView)
        val adapter = ThumbnailAdapter(files, presenter)
        val layoutManager = GridLayoutManager(context, 3)
        recycler.layoutManager = layoutManager
        recycler.adapter = adapter
        recycler.layoutManager?.scrollToPosition(files.indexOf(currentImageFileName))
        adapter.notifyDataSetChanged()

        view.cancel.setOnClickListener { v ->
            listener?.let {
                it.onCancel()
            }
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ThumbnailScreenListener) {
            listener = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setView(view)

            builder.setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })

            builder.create()

        } ?: throw IllegalStateException("missing activity!")

    }


}