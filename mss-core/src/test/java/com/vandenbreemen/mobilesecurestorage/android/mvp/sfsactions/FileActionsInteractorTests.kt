package com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions

import com.vandenbreemen.mobilesecurestorage.TestConstants
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import junit.framework.TestCase.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

/**
 *
 * @author kevin
 */
class FileActionsInteractorTests {

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
    fun `should rename a file`() {

        //  Arrange
        sfs.touch("file1")
        val interactor = FileActionsInteractor(sfs, "file1")

        //  Act
        interactor.rename("file2")

        //  Assert
        assertTrue("Should have renamed file", sfs.exists("file2"))
        assertFalse("Original file should not exist", sfs.exists("file1"))
    }

    @Test
    fun `should fail to rename file if target filename already exists`() {
        //  Arrange
        sfs.touch("file1")
        sfs.touch("file2")
        val interactor = FileActionsInteractor(sfs, "file1")

        //  Act
        val exception = assertThrows<ApplicationError> {
            interactor.rename("file2")
        }


    }

    @Test
    fun `Should return new filename after a successful rename`() {
        //  Arrange
        sfs.touch("file1")
        val interactor = FileActionsInteractor(sfs, "file1")

        //  Act/assert
        assertEquals("file2", interactor.rename("file2"))
    }

    @Test
    fun `should prevent renaming to blank filename`() {
        //  Arrange
        sfs.touch("file1")
        val interactor = FileActionsInteractor(sfs, "file1")

        //  Assert
        val error = assertThrows<ApplicationError> {
            interactor.rename("")
        }
    }


}