package com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions

import com.vandenbreemen.mobilesecurestorage.TestConstants
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 *
 * @author kevin
 */
class FileListInteractorTests {

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


    //  Listing files
    @Test
    fun `should list files in the system`() {

        //  Arrange
        sfs.touch("file1")
        val interactor = FileListInteractor(sfs)

        //  Act
        val list = interactor.getFileList()

        //  Assert
        TestCase.assertEquals(1, list.size)

    }

    @Test
    fun `file items should include file name and file type`() {

        //  Arrange
        sfs.touch("file1")
        sfs.setFileType("file1", FileTypes.DATA);
        val interactor = FileListInteractor(sfs)

        //  Act
        val list = interactor.getFileList()
        val item = list[0]

        //  Assert
        assertEquals("file1", item.name)
        assertEquals(FileTypes.DATA, item.fileType)
    }

    @Test
    fun `If a file does not have a file type then the file type should be null`() {
        //  Arrange
        sfs.touch("file1")
        val interactor = FileListInteractor(sfs)

        //  Act
        val list = interactor.getFileList()
        val item = list[0]

        //  Assert
        assertNull(item.fileType)
    }

    @Test
    fun `sorts files alphabetically in ascending order`() {
        //  Arrange
        val interactor = FileListInteractor(sfs)
        sfs.touch("a");
        sfs.touch("c");
        sfs.touch("b");


        //  Act
        val list = interactor.sortByName(true);

        //  Assert
        assertEquals(list[0].name, "a")
        assertEquals(list[1].name, "b")
        assertEquals(list[2].name, "c")
    }

    @Test
    fun `sorts files alphabetically in descending order`() {
        //  Arrange
        val interactor = FileListInteractor(sfs)
        sfs.touch("a");
        sfs.touch("c");
        sfs.touch("b");


        //  Act
        val list = interactor.sortByName(false);

        //  Assert
        assertEquals(list[0].name, "c")
        assertEquals(list[1].name, "b")
        assertEquals(list[2].name, "a")
    }

}