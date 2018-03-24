package com.vandenbreemen.secretcamera.robot

import android.app.Activity
import android.content.Intent
import android.os.Environment
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import org.robolectric.shadows.ShadowApplication
import java.io.File

/**
 * Created by kevin on 24/03/18.
 */
open class BaseRobot(private val activityClass:Class<out Activity>) {

    val sfsFile =
            File("${Environment.getExternalStorageDirectory().toString() + File.separator}_${javaClass.simpleName}_${System.currentTimeMillis()}")

    private val testPassword = SecureFileSystem.generatePassword(SecureString.fromPassword("password"))

    init {
        sfsFile.deleteOnExit()
    }

    fun sfs():SecureFileSystem{
        return object: SecureFileSystem(sfsFile){
            override fun getPassword(): SecureString = testPassword
        }
    }

    fun credentials():SFSCredentials{
        return SFSCredentials(sfsFile, testPassword)
    }

    fun intent():Intent{
        val intent = Intent(ShadowApplication.getInstance().applicationContext, activityClass)
        intent.putExtra(SFSCredentials.PARM_CREDENTIALS, credentials())
        return intent
    }

}