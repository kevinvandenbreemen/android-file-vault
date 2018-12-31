package com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions

import com.vandenbreemen.mobilesecurestorage.patterns.ProgressListener
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Presenter

/**
 *
 * @author kevin
 */
class SFSActionsPresenterImpl(val view: SFSActionsView, private val router: SFSActionsRouter, private val model: SFSActionsModel) : Presenter<SFSActionsModel, SFSActionsView>(model, view),  SFSActionsPresenter {


    var progress: ProgressListener<Long>

    init {
        progress = object: ProgressListener<Long> {
            override fun setMax(progressMax: Long) {
                view.setProgressMax(progressMax)
            }

            override fun update(progress: Long) {
                view.setCurrentProgress(progress)
            }
        }
    }

    override fun selectChangePassword() {
        router.openChangePassword()
    }

    override fun changePassword(currentPassword: String, newPassword: String, reEnterNewPassword: String) {
        model.changePassword(currentPassword, newPassword, reEnterNewPassword, progress).subscribe({ newPass ->
            router.returnToMain(model.generateCredentials(newPass))
        }, {error ->

        })
    }


    override fun setupView() {

    }
}