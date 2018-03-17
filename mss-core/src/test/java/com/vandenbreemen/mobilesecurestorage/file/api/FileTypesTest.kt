package com.vandenbreemen.mobilesecurestorage.file.api

import junit.framework.TestCase.assertEquals
import org.junit.Test

/**
 * Created by kevin on 17/03/18.
 */
class FileTypesTest {

    @Test
    fun shouldSupportRegisteringFileType(){
        FileTypes.registerFileTypes(FakeFileType.values())
        assertEquals(FakeFileType.FAKE_FILE_TYPE, FileTypes.getFileType(arrayOf(22)))

    }

}

enum class FakeFileType(override val firstByte: Byte, override val secondByte:Byte?=null):FileType{

    TEST_FILE_TYPE(Byte.MAX_VALUE,Byte.MAX_VALUE),
    FAKE_FILE_TYPE(22)

    ;
}