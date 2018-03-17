package com.vandenbreemen.mobilesecurestorage.file.api

import com.vandenbreemen.mobilesecurestorage.message.MSSRuntime

interface FileType{
    val firstByte:Byte
    val secondByte:Byte?
}

enum class FileTypes(override val firstByte:Byte, override val secondByte:Byte? = null):FileType {
    SYSTEM(1),
    UNKNOWN(1,1),
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

            return null
        }

        fun registerFileTypes(values: Array<out FileType>) {
            values.forEach { fileType->

                var fileTypeBytes:Array<Byte?> = emptyArray()

                fileType.secondByte?.let {
                    fileTypeBytes = arrayOf(fileType.firstByte, fileType.secondByte)
                } ?: run{
                    fileTypeBytes = arrayOf(fileType.firstByte)
                }

                val existingFileType:FileType? = getFileType(fileTypeBytes)
                if(existingFileType != null){
                    throw MSSRuntime("File type $fileType is exact duplicate of existing file type $existingFileType")
                }
            }
            ALL_FILE_TYPES.addAll(values)
        }
    }
}