package com.vandenbreemen.mobilesecurestorage.android.mvp.createfilesystem

import android.util.Log
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.android.task.AsyncResult
import com.vandenbreemen.mobilesecurestorage.log.SystemLog
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import org.spongycastle.util.encoders.Base64
import java.util.function.Consumer

/**
 * <h2>Intro</h2>
 * Orchestrator/controller for creating a secure file system
 * <h2>Other Details</h2>
 * @author kevin
 */
class CreateSecureFileSystemController(val model: CreateSecureFileSystemModel, val view: CreateSecureFileSystemView) {

    init {
        model.setSecureFileSystemConsumer(object : Consumer<AsyncResult<SecureFileSystem>> {
            override fun accept(t: AsyncResult<SecureFileSystem>) {
                t.error.ifPresent(Consumer {
                    SystemLog.get().error("Error creating SFS at {}", it, model.generateFile())
                    view.display(it as ApplicationError)
                })
                if (t.result != null) {
                    SystemLog.get().info("Created SFS at {}", model.generateFile())
                    val credentials: SFSCredentials = SFSCredentials(model.generateFile(), model.password)
                    view.onComplete(credentials)
                }
            }

        })
    }

    fun submitNewSFSDetails(fileName: String?, password: String, confirmPassword: String) {
        try {
            model.setFileName(fileName)
            if (password?.isBlank() || confirmPassword?.isBlank()) {
                throw ApplicationError("Password and confirm password are required");
            }
            model.setPassword(SecureFileSystem.generatePassword(SecureString(Base64.encode(password?.toByteArray(Charsets.UTF_8)))),
                    SecureFileSystem.generatePassword(SecureString(Base64.encode(confirmPassword?.toByteArray(Charsets.UTF_8))))
            )
        } catch (err: ApplicationError) {
            Log.w("CreateFileSystemUserErr", "Error setting password or filename", err);
            view.display(err)
            return
        }
        model.create()
    }

}