package com.vandenbreemen.mobilesecurestorage.file.api

enum class FileTypes(val firstByte:Byte, val secondByte:Byte? = null) {
    SYSTEM(1),
    UNKNOWN(1,1),
    ;

    companion object {

        /**
         * All known file types
         */
        val ALL_FILE_TYPES:ArrayList<Class<FileTypes>> = arrayListOf(FileTypes::class.java)

        fun getFileTypes(bytes:Array<Byte?>):FileTypes?{
            ALL_FILE_TYPES.forEach { definedFileType->definedFileType.enumConstants.forEach { enumConstant->
                if(bytes[0]!! == enumConstant.firstByte!!){
                    bytes[1]?.let { expectedSecondByte->
                        if(expectedSecondByte == enumConstant.secondByte?:-1){
                            return@getFileTypes enumConstant
                        }
                    }
                    ?:run{
                        return@getFileTypes enumConstant
                    }
                }
            } }

            return null
        }
    }
}
