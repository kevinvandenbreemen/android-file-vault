package com.vandenbreemen.mobilesecurestorage.file.api

import com.vandenbreemen.mobilesecurestorage.message.MSSRuntime
import com.vandenbreemen.standardandroidlogging.log.SystemLog

interface FileType{
    val firstByte:Byte
    val secondByte:Byte?
}

fun FileType.getBytes(): Array<Byte?> = getBytesForFileType(this)

private fun getBytesForFileType(fileType: FileType): Array<Byte?> {
    var fileTypeBytes: Array<Byte?> = emptyArray()
    fileType.secondByte?.let {
        fileTypeBytes = arrayOf(fileType.firstByte, fileType.secondByte)
    } ?: run {
        fileTypeBytes = arrayOf(fileType.firstByte)
    }
    return fileTypeBytes
}

enum class FileTypes(override val firstByte:Byte, override val secondByte:Byte? = null):FileType {
    SYSTEM(1),
    UNKNOWN(1,1),
    DATA(1, 2)
    ;

    companion object {

        /**
         * All known file types
         */
        private val ALL_FILE_TYPES:ArrayList<FileType> = arrayListOf(*FileTypes.values())

        fun getFileType(bytes:Array<Byte?>):FileType?{
            ALL_FILE_TYPES.forEach { fileType->
                if(fileType.firstByte == bytes[0]!!){
                    fileType.secondByte?.let { secondByte->
                        if(bytes.size == 2 && bytes[1] == secondByte){
                            return@getFileType fileType
                        }
                    }?:run{
                        if(bytes.size == 1) {
                            return@getFileType fileType
                        }
                    }
                }
            }

            SystemLog.get().error("Unable to get file type for bytes {${bytes[0]}, ${bytes[1]}}")

            return null
        }

        fun registerFileTypes(values: Array<out FileType>) {
            values.forEach { fileType->

                var fileTypeBytes: Array<Byte?> = fileType.getBytes()

                val existingFileType:FileType? = getFileType(fileTypeBytes)
                if(existingFileType != null){
                    throw MSSRuntime("File type $fileType is exact duplicate of existing file type $existingFileType")
                }
            }
            ALL_FILE_TYPES.addAll(values)
        }
    }
}
