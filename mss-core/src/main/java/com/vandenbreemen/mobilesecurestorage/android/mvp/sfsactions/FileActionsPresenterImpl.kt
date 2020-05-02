package com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions

import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread

/**
 *
 * @author kevin
 */
class FileActionsPresenterImpl(private val sfsPresenter: SFSActionsPresenter, private val model: FileActionsModel) : FileActionsPresenter {

    private var view: FileActionsView? = null

    override fun rename(newName: String) {
        model.rename(newName).observeOn(mainThread()).subscribe({ b ->
            view?.fileRenameSuccess(newName)
        }, { error ->
            if (error is ApplicationError) {
                view?.showError(error)
            } else {
                view?.showError(ApplicationError("Unknown error occurred"))
            }
        })
    }

    override fun setView(view: FileActionsView) {
        this.view = view
    }

    override fun start() {

    }

    override fun close() {

    }

    override fun pause() {
    }

    override fun isClosed(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}