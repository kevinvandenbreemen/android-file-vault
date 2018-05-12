package com.vandenbreemen.mobilesecurestorage.cache

import junit.framework.TestCase.*
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils
import org.cache2k.Cache2kBuilder
import org.junit.Test
import java.util.concurrent.Callable

/**
 * <h2>Intro</h2>
 *
 *
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
class CacheLearningTest {

    @Test
    fun shouldBeAbleToCacheAnObject() {
        val fileContentCacheExample = object : Cache2kBuilder<String, Any>() {

        }
                .eternal(true)
                .build()

        fileContentCacheExample.put("fileContent", "Larry")
        assertEquals("Retrieved value", "Larry", fileContentCacheExample["fileContent"])
    }

    @Test
    fun shouldBeAbleToCacheBytes() {
        val fileBytesExample = object : Cache2kBuilder<String, CachedByteData>() {

        }.build()
        fileBytesExample.put("fileContent", CachedByteData(byteArrayOf(2, 5, 0, 1)))

        assertTrue("Bytes", ByteUtils.equals(byteArrayOf(2, 5, 0, 1), fileBytesExample["fileContent"].data))

    }

    @Test
    fun shouldBeAbleToLazilyGenerateValues() {
        val fileBytesExample = object : Cache2kBuilder<String, CachedByteData>() {

        }.build()


        assertTrue("Bytes", ByteUtils.equals(byteArrayOf(2, 5, 0, 1),
                fileBytesExample.computeIfAbsent("fileContent", Callable { CachedByteData(byteArrayOf(2, 5, 0, 1)) }).data))
    }

    @Test
    fun shouldBeAbleToClearCache() {
        val fileBytesExample = object : Cache2kBuilder<String, CachedByteData>() {

        }.build()
        fileBytesExample.put("fileContent", CachedByteData(byteArrayOf(2, 5, 0, 1)))

        fileBytesExample.clear()

        assertNull("No values", fileBytesExample.get("fileContent"))
    }

    @Test
    fun shouldBeAbleToClearAndClose() {
        val fileBytesExample = object : Cache2kBuilder<String, CachedByteData>() {

        }.build()
        fileBytesExample.put("fileContent", CachedByteData(byteArrayOf(2, 5, 0, 1)))

        fileBytesExample.clear()
        fileBytesExample.clearAndClose()
    }

}
