package com.vandenbreemen.secretcamera.mvp.impl.projects

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import com.vandenbreemen.secretcamera.api.Project
import io.reactivex.Completable
import io.reactivex.Single

class ProjectListModel(credentials: SFSCredentials): Model(credentials) {
    override fun onClose() {

    }

    override fun setup() {

    }

    fun getProjects(): Single<List<Project>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun addNewProject(project: Project): Completable{
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}