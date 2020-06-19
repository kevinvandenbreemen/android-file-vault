package com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions

import com.vandenbreemen.mobilesecurestorage.TestConstants
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 *
 * @author kevin
 */
class FileDetailsInteractorTests {

    lateinit var sfs: SecureFileSystem
    lateinit var sfsFile: File

    @Before
    fun setup() {
        sfsFile = TestConstants.getTestFile("fileActionsInteractor", true)
        val tempPassword = "password"
        val testPassword = SecureFileSystem.generatePassword(SecureString.fromPassword(tempPassword))
        val credentials = SFSCredentials(sfsFile, testPassword)

        sfs = object : SecureFileSystem(credentials.fileLocation) {
            override fun getPassword(): SecureString = credentials.password
        }
    }

    @After
    fun tearDown() {
        sfsFile.delete()
    }

    @Test
    fun `should get file details`() {
        //  Arrange
        sfs.storeObject("file1", "this is a test")
        val interactor = FileDetailsInteractor(sfs, "file1")

        //  Act
        val details = interactor.getFileDetails()

        //  Assert
        assertEquals(1, details.size)
    }

}