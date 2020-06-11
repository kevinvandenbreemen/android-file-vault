package com.vandenbreemen.secretcamera.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory

/**
 *
 * @author kevin
 */
class ConfirmDialogFragmentFactory(private val fileNames: List<String>, private val callBack: () -> Unit) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {

        return ConfirmDeleteDialogFragment(fileNames) {
            callBack()
        }

    }

}