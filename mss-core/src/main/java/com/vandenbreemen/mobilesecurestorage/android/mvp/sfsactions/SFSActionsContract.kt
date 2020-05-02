package com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.PresenterContract
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.View

/**
 * Standard actions you can take on an SFS
 * @author kevin
 */

interface SFSActionsView : View {

    fun setProgressMax(max: Long)
    fun setCurrentProgress(currentProgress: Long)
    fun displayFileList(files: List<FileListItemView>)

}

interface SFSActionsRouter {

    fun returnToMain(sfsCredentials: SFSCredentials)
    fun openChangePassword()
    fun closeChangePassword()

}

interface SFSActionsPresenter : PresenterContract {

    fun selectChangePassword()
    fun changePassword(currentPassword: String, newPassword: String, reEnterNewPassword: String)
    fun cancelChangePassword()
    fun listFiles()

    /**
     * Start workflow for performing actions on a specific file
     */
    fun actionsFor(fileName: String, withView: FileActionsView): FileActionsPresenter

}

interface FileActionsPresenter : PresenterContract {

    fun rename(newName: String)
    fun setView(view: FileActionsView)

}

interface FileActionsView : View {

    fun fileRenameSuccess(newName: String)

}