package com.vandenbreemen.mobilesecurestorage.file.api

import com.vandenbreemen.mobilesecurestorage.message.MSSRuntime
import junit.framework.TestCase.assertEquals
import org.junit.BeforeClass
import org.junit.Test

/**
 * Created by kevin on 17/03/18.
 */
class FileTypesTest {

    companion object {  //  Set up file types before test (beforeClass wasn't working...)
        init{
            FileTypes.registerFileTypes(FakeFileType.values())
        }
    }

    @Test
    fun shouldSupportRegisteringFileType(){
        assertEquals(FakeFileType.FAKE_FILE_TYPE, FileTypes.getFileType(arrayOf(Byte.MAX_VALUE, 3)))

    }

    @Test(expected = MSSRuntime::class)
    fun shouldPreventRegisteringSameBytes(){
        FileTypes.registerFileTypes(FakeFileType.values())
    }

}

enum class FakeFileType(override val firstByte: Byte, override val secondByte:Byte?=null):FileType{

    TEST_FILE_TYPE(Byte.MAX_VALUE,2),
    FAKE_FILE_TYPE(Byte.MAX_VALUE, 3)

    ;
}