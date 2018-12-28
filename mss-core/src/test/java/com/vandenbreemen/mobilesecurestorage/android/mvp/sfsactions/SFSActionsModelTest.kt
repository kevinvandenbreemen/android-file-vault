package com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions

import android.os.Environment
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.ChunkedMediumException
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.patterns.ProgressListener
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class SFSActionsModelTest {

    lateinit var credentials: SFSCredentials

    lateinit var sfs: SecureFileSystem

    lateinit var sfsFile: File

    lateinit var model: SFSActionsModel

    val progress = object: ProgressListener<Long> {
        override fun setMax(progressMax: Long?) {

        }

        override fun update(progress: Long?) {

        }
    }

    @Before
    fun setup() {
        RxJavaPlugins.setComputationSchedulerHandler { scheduler -> AndroidSchedulers.mainThread() }

        sfsFile = File(Environment.getExternalStorageDirectory().toString() + File.separator + "test")
        val tempPassword = "password"
        val testPassword = SecureFileSystem.generatePassword(SecureString.fromPassword(tempPassword))
        credentials = SFSCredentials(sfsFile, testPassword)

        sfs = object : SecureFileSystem(credentials.fileLocation){
            override fun getPassword(): SecureString = credentials.password
        }

        model = SFSActionsModel(credentials)
        model.init().subscribe()
    }

    @Test
    fun shouldChangePassword() {
        //  Act
        val test = model.changePassword("password", "update", "update", progress).test()

        //  Assert
        test.assertComplete()
        test.assertNoErrors()

        try {
            credentials = SFSCredentials(sfsFile, SecureFileSystem.generatePassword(SecureString.fromPassword("password")))
            object : SecureFileSystem(credentials.fileLocation){
                override fun getPassword(): SecureString = credentials.password
            }
            fail("System should have changed password")
        } catch (exc: ChunkedMediumException){
            System.err.println(exc.localizedMessage)
        }

        val testPassword = SecureFileSystem.generatePassword(SecureString.fromPassword("update"))
        credentials = SFSCredentials(sfsFile, testPassword)

        object : SecureFileSystem(credentials.fileLocation){
            override fun getPassword(): SecureString = credentials.password
        }.listFiles()


    }

    @Test
    fun shouldPreventChangePasswordIfCurrentPasswordNotCorrect() {
        //  Act
        val test = model.changePassword("assword", "update", "update", progress).test()

        //  Assert
        test.assertNotComplete()
        test.assertError(ApplicationError::class.java)

    }

    @Test
    fun shouldProvideUpdatedPasswordOnCompleteChange() {
        //  Act
        val test = model.changePassword("password", "update", "update", progress).blockingGet()

        credentials = SFSCredentials(sfsFile, test)

        object : SecureFileSystem(credentials.fileLocation){
            override fun getPassword(): SecureString = credentials.password
        }.listFiles()
    }

}