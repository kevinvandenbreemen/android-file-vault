package com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.api.FileInfo
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.PresenterContract
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.View

/**
 * Standard actions you can take on an SFS
 * @author kevin
 */

data class SFSDetails(val totalUnits: Int, val unitsUsed: Int) {

}

interface SFSActionsView : View {

    fun setProgressMax(max: Long)
    fun setCurrentProgress(currentProgress: Long)
    fun displayFileList(files: List<FileListItemView>)
    fun displaySFSDetails(details: SFSDetails)

    /**
     * Display details about a particular file
     */
    fun displayFileDetails(fileInfo: FileInfo)

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
    fun sortFiles(ascending: Boolean)

    /**
     * Start workflow for performing actions on a specific file
     */
    fun actionsFor(fileName: String, withView: FileActionsView): FileActionsPresenter

    /**
     * Display read-only file information about a particular file
     */
    fun detailsFor(fileName: String)

    /**
     * Attempt to return to main
     */
    fun returnToMain()

}

interface FileActionsPresenter : PresenterContract {

    fun rename(newName: String)
    fun setView(view: FileActionsView)

}

interface FileActionsView : View {

    fun fileRenameSuccess(newName: String)

}