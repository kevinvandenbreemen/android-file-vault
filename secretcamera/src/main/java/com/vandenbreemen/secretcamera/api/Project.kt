package com.vandenbreemen.secretcamera.api

import java.io.Serializable

data class Project(val title: String, val details: String, val tasks: ArrayList<Task> = ArrayList()) : Serializable {
}