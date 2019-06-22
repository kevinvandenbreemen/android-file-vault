package com.vandenbreemen.mobilesecurestorage.file

import com.vandenbreemen.mobilesecurestorage.TestConstants
import junit.framework.Assert
import junit.framework.TestCase.assertEquals
import org.junit.Test

class IndexedFileStressTest {

    //@Test
    fun shouldHandleReAllocationOfFATUnits() {

        //  Arrange
        val tempFile = TestConstants.getTestFile("fillDaFat" + System.currentTimeMillis() + ".dat")
        var idf = IndexedFile(tempFile)

        val maxIteration = 10000000

        var fileName: String
        var validated = false
        for (i in 1 until maxIteration){
            fileName = "fn_$i"
            idf.storeObject(fileName, "Hello from the viper - $i")

            if(idf.fat._unitsAllocated(FAT.FILENAME).size > 1) {
                //  Now force a unit move on the FAT!
                val currentFATAllocations = idf.fat._unitsAllocated(FAT.FILENAME)
                idf.deleteFile("fn_2")
                val updatedFATAllocations = idf.fat._unitsAllocated(FAT.FILENAME)
                println("$currentFATAllocations -> $updatedFATAllocations")
                break
            }
        }

        idf = IndexedFile(tempFile)
        idf.listFiles()
        assertEquals(1, idf.fat._unitsAllocated(FAT.FILENAME).size)
        Assert.assertEquals(listOf(0L), idf.fat._unitsAllocated(FAT.FILENAME))

    }

    @Test
    fun shouldHandleRemovalOfLargeFiles() {
        //  Arrange
        val giantAssedDataSet = Array<Byte>((IndexedFile.CHUNK_SIZE*3) + 4353){ 42 }
        val smallerDataSet = Array<Byte>((IndexedFile.CHUNK_SIZE*2) + 21134) { 69 }

        val tempFile = TestConstants.getTestFile("fillDaFile" + System.currentTimeMillis() + ".dat")
        var idf = IndexedFile(tempFile)
        idf.testMode = true

        idf.storeObject("file1", giantAssedDataSet)
        idf.storeObject("file2", giantAssedDataSet)
        idf.storeObject("file3", smallerDataSet)
        idf.storeObject("file4", giantAssedDataSet)

        //  Act
        idf.deleteFiles("file2", "file4")

        //  Act
        idf.loadFile("file1")
        idf.loadFile("file3")

    }

}