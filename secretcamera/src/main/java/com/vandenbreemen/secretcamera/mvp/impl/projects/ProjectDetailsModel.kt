package com.vandenbreemen.secretcamera.mvp.impl.projects

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.api.SecureFileSystemInteractor
import com.vandenbreemen.mobilesecurestorage.file.api.SecureFileSystemInteractorFactory
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import com.vandenbreemen.secretcamera.api.Project
import com.vandenbreemen.secretcamera.api.Task
import com.vandenbreemen.standardandroidlogging.log.SystemLog
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.schedulers.Schedulers.computation

class ProjectDetailsModel(var projectName: String, credentials: SFSCredentials) : Model(credentials) {

    lateinit var sfsInteractor: SecureFileSystemInteractor

    lateinit var project: Project

    override fun onClose() {

    }

    override fun setup() {
        sfsInteractor = SecureFileSystemInteractorFactory.get(sfs)
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

    fun getTasks(): List<Task> {
        return ArrayList<Task>(project.tasks)
    }

    @Throws(ApplicationError::class)
    fun submitUpdateTaskDetails(existingTask: Task, updatedTaskValues: Task): Single<List<Task>> {
        if (updatedTaskValues.text.isBlank()) {
            throw ApplicationError("Task description is required")
        }

        return Single.create(SingleOnSubscribe<List<Task>> { subscriber ->
            existingTask.text = updatedTaskValues.text
            sfsInteractor.save(project, projectName, ProjectFileTypes.PROJECT)
            subscriber.onSuccess(project.tasks)
        }).subscribeOn(computation())
    }

    fun markTaskCompleted(task: Task, completed: Boolean): Single<List<Task>> {
        return Single.create(SingleOnSubscribe<List<Task>> { subscriber ->
            task.complete = completed
            sfsInteractor.save(project, projectName, ProjectFileTypes.PROJECT)
            subscriber.onSuccess(project.tasks)
        }).subscribeOn(computation())


    }

    @Throws(ApplicationError::class)
    fun submitUpdatedProjectDetails(projectName: String, projectDescription: String): Single<Project> {

        if (projectDescription.isBlank()) {
            throw ApplicationError("Project description is required")
        }

        if (projectName.isBlank()) {
            throw ApplicationError("Project name is required")
        }

        return Single.create(SingleOnSubscribe<Project> { subscriber ->

            val currentName = project.title
            if (currentName != projectName) {
                sfsInteractor.rename(project.title, projectName)
                this.projectName = projectName
            }

            project.details = projectDescription
            project.title = projectName
            sfsInteractor.save(project, projectName, ProjectFileTypes.PROJECT)
            subscriber.onSuccess(project)
        }).subscribeOn(computation())
    }

    fun updateItemPosition(oldPosition: Int, newPosition: Int): Completable {
        return Completable.create { subscriber ->

            val taskListCopy = ArrayList<Task>(project.tasks)

            try {
                val atOldPosition = taskListCopy.removeAt(oldPosition)
                taskListCopy.add(newPosition, atOldPosition)
            } catch (error: Exception) {

                SystemLog.get().error("${ProjectDetailsModel::class.java.simpleName} -- Failed to move task from $oldPosition to $newPosition due to", error)

                subscriber.onError(ApplicationError("Unable to move task!"))
                return@create
            }

            project.tasks.clear()
            project.tasks.addAll(taskListCopy)

            sfsInteractor.save(project, projectName, ProjectFileTypes.PROJECT)
            subscriber.onComplete()
        }.subscribeOn(computation())
    }


}
