package com.vandenbreemen.mobilesecurestorage.security.crypto.persistence

import com.vandenbreemen.mobilesecurestorage.TestConstants
import com.vandenbreemen.mobilesecurestorage.file.FileMeta
import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.extListFiles
import com.vandenbreemen.mobilesecurestorage.security.crypto.getFileMeta
import com.vandenbreemen.mobilesecurestorage.security.crypto.setFileMetadata
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
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