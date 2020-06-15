package com.vandenbreemen.secretcamera.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions.SFSDetails

/**
 *
 * @author kevin
 */
class SFSDetailsViewModel : ViewModel() {

    val totalUnitCount: LiveData<Int>
        get() = totalUnitsLiveData

    val unitsUsedCount: LiveData<Int>
        get() = unitsUsedLiveData

    private val totalUnitsLiveData: MutableLiveData<Int> = MutableLiveData()
    private val unitsUsedLiveData: MutableLiveData<Int> = MutableLiveData()

    fun setDetails(details: SFSDetails) {
        totalUnitsLiveData.postValue(details.totalUnits)
        unitsUsedLiveData.postValue(details.unitsUsed)
    }

}