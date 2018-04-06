package com.vandenbreemen.mobilesecurestorage.message

import junit.framework.TestCase.assertEquals
import org.junit.Test

/**
 * Created by kevin on 06/04/18.
 */
class ApplicationErrorTest {

    @Test
    fun shouldSupportConstructingUsingAnotherApplicationError() {
        val error1 = ApplicationError("First Error")
        val error2 = ApplicationError(error1)

        assertEquals("Copied message", "First Error", error2.localizedMessage)
    }

    @Test
    fun shouldContainCausedByWhenConstructedUsingException() {
        val error1 = ApplicationError("First Error")
        val error2 = ApplicationError(error1)

        assertEquals("Caused by", error1, error2.cause)
    }
}