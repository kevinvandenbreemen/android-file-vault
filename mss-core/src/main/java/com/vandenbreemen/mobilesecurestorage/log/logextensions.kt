package com.vandenbreemen.mobilesecurestorage.log

/**
 * Log error with the given tag
 */
fun CurrentSystemLog.error(tag: String, message: String, throwable: Throwable) {
    this.error("$tag - $message", throwable)
}