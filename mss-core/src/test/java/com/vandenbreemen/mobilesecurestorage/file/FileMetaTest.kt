package com.vandenbreemen.mobilesecurestorage.file

import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes
import junit.framework.Assert
import junit.framework.TestCase.assertEquals
import org.junit.Test

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

}