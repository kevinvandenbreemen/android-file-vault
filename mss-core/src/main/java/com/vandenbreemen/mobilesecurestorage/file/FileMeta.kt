package com.vandenbreemen.mobilesecurestorage.file

import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes

class FileMeta {

    private var fileTypeBytes:Array<Byte?> = arrayOf(FileTypes.UNKNOWN.firstByte, FileTypes.UNKNOWN.secondByte)

    fun setFileType(fileType:FileTypes){
        this.fileTypeBytes = arrayOf(fileType.firstByte, fileType.secondByte)
        fileType.secondByte?.let { fileTypeBytes = arrayOf(fileType.firstByte, fileType.secondByte) }
    }

    fun getFileType():FileTypes{
        return FileTypes.getFileTypes(fileTypeBytes)!!
    }

}
