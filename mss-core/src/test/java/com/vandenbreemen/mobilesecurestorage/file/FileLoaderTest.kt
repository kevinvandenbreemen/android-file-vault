package com.vandenbreemen.mobilesecurestorage.file

import com.vandenbreemen.mobilesecurestorage.TestConstants
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.security.Bytes
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils
import org.junit.Before
import org.junit.Test

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
class FileLoaderTest {

    lateinit var fileLoader: FileLoader

    @Before
    fun setup() {
        this.fileLoader = getFileImporter()
    }

    @Test
    fun shouldImportFileData() {
        val expectedBytes = Bytes.loadBytesFromFile(TestConstants.TEST_RES_IMG_1)
        val importedFileData = fileLoader.loadFile(TestConstants.TEST_RES_IMG_1)

        assertTrue("Imported data", ByteUtils.equals(expectedBytes, importedFileData.fileData))
    }

    @Test(expected = ApplicationError::class)
    fun shouldPreventImportingDirectory() {
        val directory = TestConstants.getTestFile("testdir_${System.currentTimeMillis()}", false)
        directory.mkdir()
        fileLoader.loadFile(directory)
    }

    @Test(expected = ApplicationError::class)
    fun shouldNotImportFileThatDoesNotExist() {
        val nonExistentFile = TestConstants.getTestFile("doesNotExist_${System.currentTimeMillis()}", false)
        fileLoader.loadFile(nonExistentFile)
    }

    @Test
    fun shouldGetFilenameForImports() {
        val expectedName = TestConstants.TEST_RES_IMG_1.name
        assertEquals("File name for import", expectedName, fileLoader.getFilenameToUseWhenImporting(TestConstants.TEST_RES_IMG_1))
    }

}