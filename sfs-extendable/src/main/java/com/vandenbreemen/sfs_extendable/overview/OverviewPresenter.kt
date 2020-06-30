package com.vandenbreemen.sfs_extendable.overview

import com.vandenbreemen.mobilesecurestoragemvp.Presenter

/**
 *
 * @author kevin
 */
class OverviewPresenter(private val model: OverviewModel) : Presenter<OverviewModel, OverviewView>(model) {

    override fun setupView() {
        getView()?.showFilesCount(model.getFilesCount())
    }
}