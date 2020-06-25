package com.vandenbreemen.secretcamera.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vandenbreemen.mobilesecurestorage.file.api.FileInfo


/**
 *
 * @author kevin
 */
class FileDetailsViewModel : ViewModel() {

    val fileInfo: LiveData<FileInfo>
        get() = fileInfoLiveData

    private val fileInfoLiveData: MutableLiveData<FileInfo> = MutableLiveData()

    fun update(data: FileInfo) {
        fileInfoLiveData.postValue(data)
    }

}