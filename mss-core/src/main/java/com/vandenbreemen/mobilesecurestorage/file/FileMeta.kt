package com.vandenbreemen.mobilesecurestorage.file

import com.vandenbreemen.mobilesecurestorage.file.api.FileType
import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes
import com.vandenbreemen.mobilesecurestorage.file.api.getBytes
import java.io.Serializable

class FileMeta() : Serializable {

    /**
     * File type bytes
     */
    private var ftb: ByteArray = ByteArray(2, { it -> if (it == 0) FileTypes.UNKNOWN.firstByte!! else FileTypes.UNKNOWN.secondByte!! })

    constructor(fileType: FileType) : this() {
        this.setFileType(fileType)
    }

    fun setFileType(fileType:FileType){
        val byteArray = fileType.getBytes()
        this.ftb = ByteArray(byteArray.size, { index -> byteArray[index]!! })
    }

    fun getFileType(): FileType {
        val bytes = Array<Byte?>(ftb.size, { it -> ftb[it] })
        return FileTypes.getFileType(bytes)!!

    }

    override fun equals(other: Any?): Boolean {
        if (other is FileMeta) {
            if (other.ftb.size != this.ftb.size) {
                return false
            }

            return other.ftb contentEquals this.ftb

        }
        return false
    }

}
