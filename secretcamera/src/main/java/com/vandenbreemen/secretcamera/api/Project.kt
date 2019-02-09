package com.vandenbreemen.secretcamera.api

import java.io.Serializable

data class Project(var title: String, var details: String, val tasks: ArrayList<Task> = ArrayList()) : Serializable {

    companion object {
        private const val serialVersionUID: Long = 123
    }

}