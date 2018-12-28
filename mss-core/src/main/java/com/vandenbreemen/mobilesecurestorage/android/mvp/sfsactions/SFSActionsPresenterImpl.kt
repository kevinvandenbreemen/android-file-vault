package com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions

/**
 *
 * @author kevin
 */
class SFSActionsPresenterImpl(val view: SFSActionsView, private val router: SFSActionsRouter) : SFSActionsPresenter {

    override fun selectChangePassword() {
        router.openChangePassword()
    }

    override fun changePassword(currentPassword: String, newPassword: String, reEnterNewPassword: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun start() {

    }

    override fun close() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isClosed(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}