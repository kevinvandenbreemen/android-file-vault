package com.vandenbreemen.secretcamera.mvp.gallery

import com.vandenbreemen.mobilesecurestorage.file.api.FileType
import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes
import com.vandenbreemen.secretcamera.api.SEC_CAM_BYTE

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
enum class PicturesFileTypes(override val firstByte: Byte, override val secondByte: Byte? = null) : FileType {

    IMPORTED_IMAGE(SEC_CAM_BYTE, 10),
    CAPTURED_IMAGE(SEC_CAM_BYTE, 11)
    ;

    companion object {
        init {
            FileTypes.registerFileTypes(values())
        }
    }

}