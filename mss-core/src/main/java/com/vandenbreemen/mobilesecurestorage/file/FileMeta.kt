package com.vandenbreemen.mobilesecurestorage.file

import com.vandenbreemen.mobilesecurestorage.file.api.FileType
import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes
import com.vandenbreemen.mobilesecurestorage.file.api.getBytes

class FileMeta {

    private var fileTypeBytes:Array<Byte?> = arrayOf(FileTypes.UNKNOWN.firstByte, FileTypes.UNKNOWN.secondByte)

    fun setFileType(fileType:FileType){
        this.fileTypeBytes = fileType.getBytes()
    }

    fun getFileType(): FileType {
        return FileTypes.getFileType(fileTypeBytes)!!
    }

}
