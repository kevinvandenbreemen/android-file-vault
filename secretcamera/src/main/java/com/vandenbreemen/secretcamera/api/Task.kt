package com.vandenbreemen.secretcamera.api

import java.io.Serializable

/**
 *
 * @author kevin
 */
data class Task(var text: String) : Serializable {

    companion object {
        private const val serialVersionUID: Long = 123
    }

    var complete: Boolean = false
}