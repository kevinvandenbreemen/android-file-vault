package com.vandenbreemen.mobilesecurestorage.android.mvp.loadfilesystem

import android.util.Log
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.ChunkedMediumException
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import java.io.File

/**
 * <h2>Intro</h2>
 * Model for loading secure file systems
 * <h2>Other Details</h2>
 * @author kevin
 */
class LoadFileSystemModel(private val file: File) {


    @Throws(ApplicationError::class)
    fun providePassword(password: String): SFSCredentials {

        val generatedPass = SecureFileSystem.generatePassword(
                SecureString.fromPassword(password)
        )
        try {
            object : SecureFileSystem(file) {
                override fun getPassword(): SecureString {
                    return generatedPass
                }
            }
            return SFSCredentials(file, generatedPass)
        } catch (exce: ChunkedMediumException) {
            Log.e("LoadSFS", "Failed to load with password", exce)
            throw ApplicationError("Bad password")
        }
    }

}