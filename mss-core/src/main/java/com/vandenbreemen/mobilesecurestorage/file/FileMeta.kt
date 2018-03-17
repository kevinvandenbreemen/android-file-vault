package com.vandenbreemen.mobilesecurestorage.file

import com.vandenbreemen.mobilesecurestorage.file.api.FileType
import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes

class FileMeta {

    private var fileTypeBytes:Array<Byte?> = arrayOf(FileTypes.UNKNOWN.firstByte, FileTypes.UNKNOWN.secondByte)

    fun setFileType(fileType:FileType){

        fileType.secondByte?.let {
            this.fileTypeBytes = arrayOf(fileType.firstByte, fileType.secondByte)
        } ?: run{
            this.fileTypeBytes = arrayOf(fileType.firstByte)
        }

    }

    fun getFileType(): FileType {
        return FileTypes.getFileType(fileTypeBytes)!!
    }

}
