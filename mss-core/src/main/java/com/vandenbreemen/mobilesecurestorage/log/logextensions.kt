package com.vandenbreemen.mobilesecurestorage.log

/**
 * Log error with the given tag
 */
fun CurrentSystemLog.e(tag: String, message: String, throwable: Throwable) {
    this.error("$tag - $message", throwable)
}

fun CurrentSystemLog.d(tag: String, message: String) {
    this.debug("$tag - $message")
}