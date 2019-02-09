package com.vandenbreemen.mobilesecurestorage.file.api

import android.os.Environment
import com.vandenbreemen.mobilesecurestorage.TestConstants
import com.vandenbreemen.mobilesecurestorage.file.getFileImporter
import com.vandenbreemen.mobilesecurestorage.security.Bytes
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.listFiles
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import junit.framework.TestCase.*
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
@RunWith(RobolectricTestRunner::class)
class SecureFileSystemInteractorTest {

    lateinit var sfsFile: File

    lateinit var sut: SecureFileSystemInteractor

    @Before
    fun setup() {
        sfsFile = File(Environment.getExternalStorageDirectory().toString() + File.separator + "sfs")
        sut = getSecureFileSystemInteractor(sfs())
    }

    fun createPassword(): SecureString {
        return SecureFileSystem.generatePassword(SecureString.fromPassword("password"))
    }

    fun sfs(): SecureFileSystem {
        return object : SecureFileSystem(sfsFile) {
            override fun getPassword(): SecureString {
                return createPassword()
            }

        }
    }

    @Test
    fun shouldImportFile() {
        sut.importToFile(getFileImporter().loadFile(TestConstants.TEST_RES_IMG_1), "1.jpg", null)
        assertEquals("File imported", 1, sfs().listFiles().size)
    }

    @Test
    fun shouldReturnTrueWhenImportingFileSuccessfully() {
        assertTrue("Return true", sut.importToFile(getFileImporter().loadFile(TestConstants.TEST_RES_IMG_1), "1.jpg", null))
    }

    @Test
    fun shouldStoreFileWithGivenName() {
        sut.importToFile(getFileImporter().loadFile(TestConstants.TEST_RES_IMG_1), "1.jpg", null)
        assertEquals("Imported filename", "1.jpg", sfs().listFiles()[0])
    }

    @Test
    fun shouldProperlyPersistData() {
        val rawBytes = Bytes.loadBytesFromFile(TestConstants.TEST_RES_IMG_1)
        sut.importToFile(getFileImporter().loadFile(TestConstants.TEST_RES_IMG_1), "1.jpg", null)
        val loadedBytes = sfs().loadBytesFromFile("1.jpg")
        assertTrue("Persisted bytes", ByteUtils.equals(rawBytes, loadedBytes))
    }

    @Test
    fun shouldNotOverwriteExistingFile() {
        sfs().storeObject("1.jpg", ArrayList<String>())
        sut = getSecureFileSystemInteractor(sfs())
        sut.importToFile(getFileImporter().loadFile(TestConstants.TEST_RES_IMG_1), "1.jpg", null)
    }

    @Test
    fun shouldReturnFalseWhenUnsuccessfulAtImporting() {
        sfs().storeObject("1.jpg", ArrayList<String>())
        sut = getSecureFileSystemInteractor(sfs())
        assertFalse("Expect false", sut.importToFile(getFileImporter().loadFile(TestConstants.TEST_RES_IMG_1), "1.jpg", null))
    }

    @Test
    fun shouldStoreToFileWithType() {
        val settings = "SettingsDummy"
        sut.save(settings, "settings", FileTypes.DATA)
        assertEquals("Load file", "SettingsDummy", sut.load("settings", FileTypes.DATA))
    }

    @Test
    fun shouldRenameFilePreservingType() {

        //  Arrange
        val settings = "SettingsDummy"
        sut.save(settings, "settings", FileTypes.DATA)

        //  Act
        sut.rename("settings", "diffName")

        //  Assert
        assertEquals(1, sfs().listFiles(FileTypes.DATA).size)
        assertEquals(listOf("diffName"), sfs().listFiles(FileTypes.DATA))

    }

}