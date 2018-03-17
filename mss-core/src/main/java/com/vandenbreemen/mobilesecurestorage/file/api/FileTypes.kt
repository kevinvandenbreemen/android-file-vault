package com.vandenbreemen.mobilesecurestorage.file.api

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
        val ALL_FILE_TYPES:ArrayList<FileType> = arrayListOf(*FileTypes.values())

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
    }
}
