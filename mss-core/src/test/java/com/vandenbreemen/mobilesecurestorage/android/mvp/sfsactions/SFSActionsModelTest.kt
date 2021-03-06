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
import junit.framework.TestCase.assertEquals
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

        sfs = object : SecureFileSystem(credentials.fileLocation) {
            override fun getPassword(): SecureString = credentials.password
        }

        model = SFSActionsModel(credentials)
        model.init().subscribe()
    }

    fun `Gets Details on Unit and Units Used Etc`() {

        //  Arrange
        sfs.touch("file1")
        sfs.touch("file2")
        sfs.deleteFile("file2")

        model = SFSActionsModel(credentials)

        //  Act
        val details = model.sfsDetails

        //  Assert
        assertEquals(4, details.totalUnits)
        assertEquals(3, details.unitsUsed)

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

    @Test
    fun shouldCreateCredentialsUsingChangedPassword() {
        //  Arrange
        val test = model.changePassword("password", "update", "update", progress).blockingGet()

        //  Act
        val credentials = model.generateCredentials(test)

        //  Assert
        object : SecureFileSystem(credentials.fileLocation){
            override fun getPassword(): SecureString = credentials.password
        }.listFiles()
    }

    @Test
    fun shouldPreventChangePasswordIfPasswordsDoNotMatch() {
        //  Act
        val test = model.changePassword("password", "update", "update1", progress).test()

        //  Assert
        test.assertNotComplete()
        test.assertError(ApplicationError::class.java)
    }

    @Test
    fun shouldPreventChangePasswordIfNewPasswordIsBlank() {
        //  Act
        val test = model.changePassword("password", "", "", progress).test()

        //  Assert
        test.assertNotComplete()
        test.assertError(ApplicationError::class.java)
    }

}