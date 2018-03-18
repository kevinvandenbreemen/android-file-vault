package com.vandenbreemen.mobilesecurestorage.file

import com.vandenbreemen.mobilesecurestorage.TestConstants
import com.vandenbreemen.mobilesecurestorage.data.Serialization
import com.vandenbreemen.mobilesecurestorage.file.api.FileType
import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes
import junit.framework.Assert
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Test
import java.nio.charset.Charset

/**
 * <br/>Created by kevin on 17/03/18.
 */
class FileMetaTest {

    @Test
    fun shouldStoreFileType(){
        val fileMeta = FileMeta()
        fileMeta.setFileType(FileTypes.SYSTEM)

        assertEquals(FileTypes.SYSTEM, fileMeta.getFileType())
    }

    @Test
    fun shouldBeUnknownFileTypeByDefault(){
        val fileMeta = FileMeta()
        Assert.assertEquals(FileTypes.UNKNOWN, fileMeta.getFileType())
    }

    @Test
    fun shouldSupportAdditionalFileTypes(){

        FileTypes.registerFileTypes(FakeFileType.values())

        val fileMeta = FileMeta()
        fileMeta.setFileType(FakeFileType.TEST_FILE_TYPE)

        assertEquals("Additional file type", FakeFileType.TEST_FILE_TYPE, fileMeta.getFileType())
    }

    @Test
    fun shouldBeAbleToStoreToFile() {
        val fileMeta = FileMeta()
        fileMeta.setFileType(FileTypes.SYSTEM)

        val indexedFile = IndexedFile(TestConstants.getTestFile("storeFileMeta"))
        indexedFile.storeObject("test", fileMeta)
    }

    @Test
    fun shouldSerialize() {
        val fileMeta = FileMeta()
        fileMeta.setFileType(FileTypes.SYSTEM)
        println(Serialization.toBytes(fileMeta).toString(Charset.defaultCharset()))
    }

    @Test
    fun shouldDeserialize() {
        val fileMeta = FileMeta()
        fileMeta.setFileType(FileTypes.SYSTEM)

        val serialized = Serialization.toBytes(fileMeta)

        val retrieved = Serialization.deserializeBytes(serialized) as FileMeta
        assertNotNull("retrieved", retrieved)
        assertEquals("same data", fileMeta, retrieved)
        assertEquals("Same type", FileTypes.SYSTEM, retrieved.getFileType())

    }

    @Test
    fun shouldBeAbleToRetrieveFromFile() {
        val fileMeta = FileMeta()
        fileMeta.setFileType(FileTypes.SYSTEM)

        val indexedFile = IndexedFile(TestConstants.getTestFile("retrievedFileMeta"))
        indexedFile.storeObject("test", fileMeta)

        val retrieved = indexedFile.loadFile("test") as FileMeta
        assertEquals("same data", fileMeta, retrieved)
        assertEquals("Same type", FileTypes.SYSTEM, retrieved.getFileType())
    }

}

enum class FakeFileType(override val firstByte: Byte, override val secondByte:Byte?=null):FileType{

    TEST_FILE_TYPE(Byte.MAX_VALUE,4)

    ;
}