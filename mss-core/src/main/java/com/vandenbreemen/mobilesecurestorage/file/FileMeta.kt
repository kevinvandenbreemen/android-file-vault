package com.vandenbreemen.mobilesecurestorage.file

import com.vandenbreemen.mobilesecurestorage.file.api.FileType
import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes
import com.vandenbreemen.mobilesecurestorage.file.api.getBytes
import java.io.Serializable

class FileMeta : Serializable {

    private var fileTypeBytes: ByteArray = ByteArray(2, { it -> if (it == 0) FileTypes.UNKNOWN.firstByte!! else FileTypes.UNKNOWN.secondByte!! })

    fun setFileType(fileType:FileType){
        val byteArray = fileType.getBytes()
        this.fileTypeBytes = ByteArray(byteArray.size, { index -> byteArray[index]!! })
    }

    fun getFileType(): FileType {
        val bytes = Array<Byte?>(fileTypeBytes.size, { it -> fileTypeBytes[it] })
        return FileTypes.getFileType(bytes)!!

    }

    override fun equals(other: Any?): Boolean {
        if (other is FileMeta) {
            if (other.fileTypeBytes.size != this.fileTypeBytes.size) {
                return false
            }

            return other.fileTypeBytes contentEquals this.fileTypeBytes

        }
        return false
    }

}
