package com.vandenbreemen.mobilesecurestorage.file

import com.vandenbreemen.mobilesecurestorage.file.api.FileType
import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes
import junit.framework.Assert
import junit.framework.TestCase.assertEquals
import org.junit.Test
import kotlin.reflect.KClass

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

}

enum class FakeFileType(override val firstByte: Byte, override val secondByte:Byte?=null):FileType{

    TEST_FILE_TYPE(Byte.MAX_VALUE,4)

    ;
}