package com.vandenbreemen.secretcamera.mvp.impl.projects

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.api.SecureFileSystemInteractor
import com.vandenbreemen.mobilesecurestorage.file.api.getSecureFileSystemInteractor
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import com.vandenbreemen.mobilesecurestorage.security.crypto.listFiles
import com.vandenbreemen.secretcamera.api.Project
import io.reactivex.Completable
import io.reactivex.Single

class ProjectListModel(credentials: SFSCredentials): Model(credentials) {

    lateinit var sfsInteractor: SecureFileSystemInteractor

    override fun onClose() {

    }

    override fun setup() {
        sfsInteractor = getSecureFileSystemInteractor(sfs)
    }

    fun getProjects(): Single<List<Project>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun addNewProject(project: Project): Completable{
        return Completable.create { subscriber->

            val projectTitle = project.title
            if(projectTitle.isBlank()){
                subscriber.onError(ApplicationError("Project name is required"))
                return@create
            }

            sfs.listFiles(ProjectFileTypes.PROJECT).filter { fileName->fileName.toUpperCase().equals(projectTitle.toUpperCase()) }
                    .firstOrNull()?.let {
                        subscriber.onError(ApplicationError("Project named $projectTitle already exists"))
                        return@create
                    }

            sfsInteractor.save(project, projectTitle, ProjectFileTypes.PROJECT)
            subscriber.onComplete()
        }
    }


}