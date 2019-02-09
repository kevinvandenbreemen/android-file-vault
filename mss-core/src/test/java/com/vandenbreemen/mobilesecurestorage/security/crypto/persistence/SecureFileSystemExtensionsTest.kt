package com.vandenbreemen.mobilesecurestorage.security.crypto.persistence

import com.vandenbreemen.mobilesecurestorage.TestConstants
import com.vandenbreemen.mobilesecurestorage.file.FileMeta
import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.extListFiles
import com.vandenbreemen.mobilesecurestorage.security.crypto.getFileMeta
import com.vandenbreemen.mobilesecurestorage.security.crypto.listFiles
import com.vandenbreemen.mobilesecurestorage.security.crypto.setFileMetadata
import junit.framework.TestCase.*
import org.junit.Test
import java.util.function.Supplier

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
class SecureFileSystemExtensionsTest {

    private fun getSUT(): SecureFileSystem {
        val pass = SecureString("PASS".toByteArray())
        return object : SecureFileSystem(TestConstants.getTestFile("sfsext_" + System.currentTimeMillis())) {
            override fun getPassword(): SecureString {
                return SecureFileSystem.generatePassword(pass)
            }

        }
    }

    @Test
    fun shouldStoreFileMetadata() {
        val sfs = getSUT()
        sfs.storeObject("test", "Ramana")

        sfs.setFileMetadata("test", FileMeta())

        val retrieved = sfs.getFileMeta("test")
        assertNotNull("File metadata", retrieved)
    }

    @Test
    fun shouldUpdateFileMetadataOnFileRename() {
        val sfs = getSUT()
        sfs.storeObject("test", "Ramana")

        val metadata = FileMeta(FileTypes.DATA)
        sfs.setFileMetadata("test", metadata)

        sfs.rename("test", "update")
        val retrieved = sfs.getFileMeta("update")
        assertNotNull("File metadata for renamed", retrieved)
        assertEquals(FileTypes.DATA, retrieved!!.getFileType())

    }

    @Test
    fun shouldPersistFileType() {
        val sfs = getSUT()
        sfs.storeObject("test", "Ramana")

        val fileMeta = FileMeta()
        fileMeta.setFileType(FileTypes.DATA)
        sfs.setFileMetadata("test", fileMeta)

        val retrieved = sfs.getFileMeta("test")
        assertEquals("File type", FileTypes.DATA, retrieved?.getFileType())
    }

    @Test
    fun shouldListFilesByType() {
        val sfs = getSUT()
        sfs.storeObject("test", "Ramana")
        sfs.setFileMetadata("test", FileMeta(FileTypes.DATA))

        sfs.storeObject("notInclude", "Not there")
        sfs.setFileMetadata("notInclude", FileMeta(FileTypes.SYSTEM))

        val filesList = sfs.listFiles(FileTypes.DATA)
        assertEquals("Single file", 1, filesList.size)
        assertTrue("Expected file", filesList.contains("test"))
    }

    @Test
    fun shouldListFilesByMultipleTypes() {
        val sfs = getSUT()
        sfs.storeObject("test", "Ramana")
        sfs.setFileMetadata("test", FileMeta(FileTypes.DATA))

        sfs.storeObject("test2", "OtherType")
        sfs.setFileMetadata("test2", FileMeta(FileTypes.SYSTEM))

        val filesList = sfs.listFiles(FileTypes.DATA, FileTypes.SYSTEM)
        assertEquals("Files list", 2, filesList.size)
    }

    @Test
    fun shouldProvideFileMetaCreation() {
        val sfs = getSUT()
        sfs.storeObject("test", "Ramana")

        var metadata = sfs.getFileMeta("test", Supplier { FileMeta(FileTypes.DATA) })
        assertEquals("File type", FileTypes.DATA, metadata.getFileType())

        val retrieved = sfs.getFileMeta("test")
        assertEquals("File type", FileTypes.DATA, retrieved?.getFileType())
    }

    @Test
    fun shouldNotIncludeMetadataFile() {
        val sfs = getSUT()
        sfs.storeObject("test", "Ramana")

        sfs.setFileMetadata("test", FileMeta())
        assertEquals("Single file", 1, sfs.extListFiles().size)

    }

}