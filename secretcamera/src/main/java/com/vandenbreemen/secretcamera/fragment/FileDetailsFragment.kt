package com.vandenbreemen.secretcamera.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.vandenbreemen.secretcamera.R
import com.vandenbreemen.secretcamera.mvvm.FileDetailsViewModel
import kotlinx.android.synthetic.main.layout_sfs_item_details.view.*
import java.text.SimpleDateFormat

/**
 * Fragment for displaying details on a specific file
 * @author kevin
 */
class FileDetailsFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewModel: FileDetailsViewModel by activityViewModels()

        val view = inflater.inflate(R.layout.layout_sfs_item_details, container, false)
        viewModel.fileInfo.observe(viewLifecycleOwner, Observer { info ->
            view.fileName.text = info.fileName
            view.fileSize.text = info.size.toString()
            view.createDate.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(info.createDate.time)
        })

        return view

    }

}