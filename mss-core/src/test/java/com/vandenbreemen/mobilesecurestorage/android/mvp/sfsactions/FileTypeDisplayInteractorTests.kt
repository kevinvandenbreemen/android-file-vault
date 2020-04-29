package com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions

import com.vandenbreemen.mobilesecurestorage.TestConstants
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 *
 * @author kevin
 */
class FileTypeDisplayInteractorTests {

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

    @Test
    fun `get icon for unknown file type should return unknown icon`() {

        //  Arrange
        sfs.touch("unknown")
        val interactor = FileTypeDisplayInteractor(sfs)

        //  Act
        val icon = interactor.iconFor("unknown")

        //  Assert
        assertEquals(CoreFileTypeIcons.UNKNOWN, icon)

    }

    @Test
    fun `get icon for system file should return system icon`() {
        //  Arrange
        sfs.touch("sys")
        sfs.setFileType("sys", FileTypes.SYSTEM)
        val interactor = FileTypeDisplayInteractor(sfs)


        //  Act
        val icon = interactor.iconFor("sys")

        //  Assert
        assertEquals(CoreFileTypeIcons.SYSTEM, icon)
    }

    @Test
    fun `get icon for data file should return data icon`() {
        //  Arrange
        sfs.touch("sys")
        sfs.setFileType("sys", FileTypes.DATA)
        val interactor = FileTypeDisplayInteractor(sfs)


        //  Act
        val icon = interactor.iconFor("sys")

        //  Assert
        assertEquals(CoreFileTypeIcons.DATA, icon)
    }

}