package com.vandenbreemen.secretcamera.mvp.impl.projects

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.api.SecureFileSystemInteractor
import com.vandenbreemen.mobilesecurestorage.file.api.SecureFileSystemInteractorFactory
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import com.vandenbreemen.mobilesecurestorage.security.crypto.extListFiles
import com.vandenbreemen.mobilesecurestorage.security.crypto.listFiles
import com.vandenbreemen.secretcamera.api.Project
import io.reactivex.Completable
import io.reactivex.CompletableOnSubscribe
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.computation

class ProjectListModel(credentials: SFSCredentials): Model(credentials) {

    lateinit var sfsInteractor: SecureFileSystemInteractor

    override fun onClose() {

    }

    override fun setup() {
        sfsInteractor = SecureFileSystemInteractorFactory.get(sfs)
    }

    fun getProjects(): Single<List<Project>> {
        return Single.create(SingleOnSubscribe<List<Project>> {  subscriber ->
            val fileNames = sfs.listFiles(ProjectFileTypes.PROJECT)
            subscriber.onSuccess(fileNames.map { fileName -> sfs.loadFile(fileName) as Project })
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
    }

    fun addNewProject(project: Project): Completable{
        return Completable.create(CompletableOnSubscribe { subscriber->

            val projectTitle = project.title
            if(projectTitle.isBlank()){
                subscriber.onError(ApplicationError("Project name is required"))
                return@CompletableOnSubscribe
            }

            sfs.extListFiles().asSequence().filter { fileName -> fileName.toUpperCase().equals(project.title.toUpperCase()) }
                            .firstOrNull() ?.let { match ->
                                subscriber.onError(ApplicationError("File named $match already exists"))
                                return@CompletableOnSubscribe
                            }

            sfs.listFiles(ProjectFileTypes.PROJECT).filter { fileName->fileName.toUpperCase().equals(projectTitle.toUpperCase()) }
                    .firstOrNull()?.let {
                        subscriber.onError(ApplicationError("Project named $projectTitle already exists"))
                        return@CompletableOnSubscribe
                    }

            sfsInteractor.save(project, projectTitle, ProjectFileTypes.PROJECT)
            subscriber.onComplete()
        }).subscribeOn(computation()).observeOn(AndroidSchedulers.mainThread())
    }


}