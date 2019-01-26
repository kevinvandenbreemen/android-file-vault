package com.vandenbreemen.secretcamera.mvp.impl.projects

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.api.SecureFileSystemInteractor
import com.vandenbreemen.mobilesecurestorage.file.api.getSecureFileSystemInteractor
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import com.vandenbreemen.secretcamera.api.Project
import com.vandenbreemen.secretcamera.api.Task
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.schedulers.Schedulers.computation

class ProjectDetailsModel(val projectName: String, credentials: SFSCredentials): Model(credentials) {

    lateinit var sfsInteractor: SecureFileSystemInteractor

    lateinit var project: Project

    override fun onClose() {

    }

    override fun setup() {
        sfsInteractor = getSecureFileSystemInteractor(sfs)
        project = sfsInteractor.load(projectName, ProjectFileTypes.PROJECT) as Project
    }

    fun getDescription(): String {
        return project.details
    }

    fun getProjectTitle(): String {
        return project.title
    }

    @Throws(ApplicationError::class)
    fun addTask(task: Task): Single<List<Task>> {

        if (task.text.isBlank()) {
            throw ApplicationError("Task description is required")
        }

        return Single.create(SingleOnSubscribe<List<Task>> { subscriber ->
            project.tasks.add(task)
            sfsInteractor.save(project, projectName, ProjectFileTypes.PROJECT)
            subscriber.onSuccess(project.tasks)
        }).subscribeOn(computation())
    }


}
