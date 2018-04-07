package com.vandenbreemen.mobilesecurestorage.file

import com.vandenbreemen.mobilesecurestorage.TestConstants
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.security.Bytes
import junit.framework.TestCase.assertTrue
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
class FileImporterTest {

    lateinit var fileImporter: FileImporter

    lateinit var directory: File

    @Before
    fun setup() {
        this.fileImporter = getFileImporter()
        this.directory = TestConstants.getTestFile("testdir_${System.currentTimeMillis()}", false)
        if (!this.directory.mkdir()) {
            throw RuntimeException("Failed to create test dir")
        }
    }

    @Test
    fun shouldImportFileData() {
        val expectedBytes = Bytes.loadBytesFromFile(TestConstants.TEST_RES_IMG_1)
        val importedFileData = fileImporter.importFile(TestConstants.TEST_RES_IMG_1)

        assertTrue("Imported data", ByteUtils.equals(expectedBytes, importedFileData.fileData))
    }

    @Test(expected = ApplicationError::class)
    fun shouldPreventImportingDirectory() {
        fileImporter.importFile(directory)
    }

    @Test(expected = ApplicationError::class)
    fun shouldNotImportFileThatDoesNotExist() {
        val nonExistentFile = TestConstants.getTestFile("doesNotExist_${System.currentTimeMillis()}", false)
        fileImporter.importFile(nonExistentFile)
    }

}