package com.vandenbreemen.secretcamera.mvp.impl.projects

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.api.SecureFileSystemInteractor
import com.vandenbreemen.mobilesecurestorage.file.api.getSecureFileSystemInteractor
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import com.vandenbreemen.secretcamera.api.Project

class ProjectDetailsModel(val projectName: String, credentials: SFSCredentials): Model(credentials) {

    lateinit var sfsInteractor: SecureFileSystemInteractor

    lateinit var project: Project

    override fun onClose() {

    }

    override fun setup() {
        sfsInteractor = getSecureFileSystemInteractor(sfs)

    }

    fun getDescription(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
