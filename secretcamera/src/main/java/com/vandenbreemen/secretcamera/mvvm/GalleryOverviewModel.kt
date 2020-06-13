package com.vandenbreemen.secretcamera.mvvm

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 *
 * @author kevin
 */
class GalleryOverviewModel : ViewModel() {

    val previewImages: LiveData<List<Bitmap>>
        get() = bitmapLiveData

    private var bitmapLiveData: MutableLiveData<List<Bitmap>> = MutableLiveData()

    fun setBitmapData(bitmaps: List<Bitmap>) {
        bitmapLiveData.postValue(bitmaps)
    }

}

class GalleryOverviewViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (!modelClass.isAssignableFrom(GalleryOverviewModel::class.java)) {
            throw RuntimeException("Inappropriate view model type for this factory")
        }

        return GalleryOverviewModel() as T
    }
}