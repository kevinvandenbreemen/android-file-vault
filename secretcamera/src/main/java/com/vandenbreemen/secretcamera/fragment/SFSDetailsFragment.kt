package com.vandenbreemen.secretcamera.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.vandenbreemen.secretcamera.R
import com.vandenbreemen.secretcamera.mvvm.SFSDetailsViewModel
import kotlinx.android.synthetic.main.layout_sfs_details.view.*

/**
 *
 * @author kevin
 */
class SFSDetailsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_sfs_details, container, false)

        val viewModel: SFSDetailsViewModel by activityViewModels<SFSDetailsViewModel>()
        viewModel.totalUnitCount.observe(this, Observer { count ->
            view.numUnits.text = count.toString()
        })
        viewModel.unitsUsedCount.observe(this, Observer { count ->
            view.unitsUsed.text = count.toString()
        })

        return view
    }

}