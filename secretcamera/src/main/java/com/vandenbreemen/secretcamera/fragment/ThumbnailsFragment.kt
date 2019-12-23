package com.vandenbreemen.secretcamera.fragment

import android.animation.Animator
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.secretcamera.R
import com.vandenbreemen.secretcamera.ThumbnailAdapter
import com.vandenbreemen.secretcamera.mvp.gallery.PictureViewRouter
import com.vandenbreemen.secretcamera.mvp.gallery.PictureViewerPresenter
import kotlinx.android.synthetic.main.layout_gallery_selector.*
import kotlinx.android.synthetic.main.layout_gallery_selector.view.*

/**
 *
 * @author kevin
 */
class ThumbnailsFragment(private val files: List<String>, private val currentImageFileName: String,
                         private val presenter: PictureViewerPresenter) : DialogFragment(), PictureViewRouter {

    var listener: ThumbnailScreenListener? = null
    lateinit var adapter: ThumbnailAdapter
    lateinit var recycler: RecyclerView

    interface ThumbnailScreenListener {
        fun onCancel()
        fun providePictureViewRouterDelegate(router: PictureViewRouter)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val layoutManager = GridLayoutManager(context, 5)
            recycler.layoutManager = layoutManager
            recycler.layoutManager?.scrollToPosition(files.indexOf(currentImageFileName))
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val layoutManager = GridLayoutManager(context, 3)
            recycler.layoutManager = layoutManager
            recycler.layoutManager?.scrollToPosition(files.indexOf(currentImageFileName))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.layout_gallery_selector, container, false)
        if (view == null) {
            return view
        }

        recycler = view.findViewById<RecyclerView>(R.id.imageRecyclerView)
        adapter = ThumbnailAdapter(files, presenter)
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

        view.delete.setOnClickListener { v ->
            presenter.deleteSelected()
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ThumbnailScreenListener) {
            listener = context
            context.providePictureViewRouterDelegate(this)
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

    override fun showActions() {

        val actionButtons: List<View> = listOf(delete)
        actionButtons.forEach { button ->
            button.visibility = VISIBLE
            button.animate().alpha(1.0f).setDuration(300).start()
        }

    }

    override fun hideActions() {
        val actionButtons: List<View> = listOf(delete)
        actionButtons.forEach { button ->
            button.animate().alpha(0.0f).setDuration(300).setListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {
                    button.visibility = GONE
                }

                override fun onAnimationCancel(animation: Animator?) {

                }

                override fun onAnimationStart(animation: Animator?) {

                }

            }).start()
        }
    }

    override fun enableSelectMultiple() {
        adapter.selectEnabled = true
        adapter.notifyDataSetChanged()
    }

    override fun disableSelectMultiple() {
        adapter.selectEnabled = false
        adapter.notifyDataSetChanged()
    }

    override fun navigateBack(sfsCredentials: SFSCredentials) {

    }
}