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

    val fileUnits: LiveData<Int>
        get() = fileUnitsLiveData

    private val fileUnitsLiveData: MutableLiveData<Int> = MutableLiveData()

    fun update(data: FileInfo) {
        fileUnitsLiveData.postValue(data.size)
    }

}