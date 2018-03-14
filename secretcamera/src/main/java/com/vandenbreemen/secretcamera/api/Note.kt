package com.vandenbreemen.secretcamera.api

import java.io.Serializable

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
data class Note(val title: String, val content: String) : Serializable {
}