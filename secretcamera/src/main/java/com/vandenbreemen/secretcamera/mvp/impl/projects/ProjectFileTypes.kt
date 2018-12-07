package com.vandenbreemen.secretcamera.mvp.impl.projects

import com.vandenbreemen.mobilesecurestorage.file.api.FileType
import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes.Companion.registerFileTypes
import com.vandenbreemen.secretcamera.api.PROJ_BYTE

enum class ProjectFileTypes(override val firstByte: Byte, override val secondByte: Byte? = null) : FileType {

    PROJECT(PROJ_BYTE, 1)

    ;

    companion object {
        init {
            registerFileTypes(values())
        }
    }

}