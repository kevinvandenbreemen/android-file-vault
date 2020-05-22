package com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.message.MSSRuntime
import com.vandenbreemen.mobilesecurestorage.patterns.ProgressListener
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.computation

/**
 * Application logic for performing actions involving the entire file system
 */
class SFSActionsModel(credentials: SFSCredentials): Model(credentials) {

    private lateinit var fileListInteractor: FileListInteractor
    private lateinit var fileTypeDisplayInteractor: FileTypeDisplayInteractor

    override fun onClose() {

    }

    override fun setup() {
        this.fileListInteractor = FileListInteractor(sfs)
        this.fileTypeDisplayInteractor = FileTypeDisplayInteractor(sfs)
    }

    fun changePassword(currentPassword: String, newPassword: String, reEnterNewPassword: String, progress: ProgressListener<Long>): Single<SecureString> {
        return Single.create(SingleOnSubscribe<SecureString> { subscriber ->

            if (newPassword.isBlank()) {
                subscriber.onError(ApplicationError("Please specify a new password"))
                return@SingleOnSubscribe
            }

            if(newPassword != reEnterNewPassword) {
                subscriber.onError(ApplicationError("Passwords do not match"))
                return@SingleOnSubscribe
            }

            if(!sfs.testPassword(SecureString.fromPassword(currentPassword))){
                subscriber.onError(ApplicationError("Current password is not correct"))
                return@SingleOnSubscribe
            }

            val updatedPassword = SecureFileSystem.generatePassword(SecureString.fromPassword(newPassword))
            sfs.changePassword(progress, updatedPassword)
            subscriber.onSuccess(updatedPassword)
        }).subscribeOn(computation())
    }

    fun generateCredentials(password: SecureString): SFSCredentials {

        var copied = copyCredentials()
        val ret = SFSCredentials(copied.fileLocation, password)

        copied.finalize()
        return  ret
    }

    fun listFiles(): Single<List<FileListItemView>> {
        return Single.create(SingleOnSubscribe<List<FileListItemView>> { subscriber ->
            subscriber.onSuccess(this.fileListInteractor.fileList.map { item ->
                item.icon = fileTypeDisplayInteractor.iconFor(item.name)
                item
            })
            return@SingleOnSubscribe
        }).subscribeOn(computation()).observeOn(mainThread())
    }

    fun sortFiles(ascending: Boolean): Single<List<FileListItemView>> {
        return Single.create(SingleOnSubscribe<List<FileListItemView>> { subscriber ->
            subscriber.onSuccess(this.fileListInteractor.sortByName(ascending).map { item ->
                item.icon = fileTypeDisplayInteractor.iconFor(item.name)
                item
            })
            return@SingleOnSubscribe
        }).subscribeOn(computation()).observeOn(mainThread())
    }

    /**
     * Create file actions model for a specific file
     */
    fun fileActionsModel(fileName: String): FileActionsModel {
        if (!sfs.exists(fileName)) {
            throw MSSRuntime("File $fileName does not exist")
        }

        return FileActionsModel(FileActionsInteractor(sfs, fileName))
    }
}
