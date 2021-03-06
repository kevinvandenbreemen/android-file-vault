package com.vandenbreemen.secretcamera.mvvm

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.TimeUnit

class PictureViewerFunctionsViewModel: ViewModel() {

    private var slideShowDelaySeconds: Long = 1
    val currentSlideshowDelaySeconds: Long get() = slideShowDelaySeconds

    private val slideShowHandler = Handler(Looper.getMainLooper())
    private lateinit var slideShowTask: Runnable

    private val nextImage: MutableLiveData<Unit> = MutableLiveData()
    val nextImageLiveData: LiveData<Unit> get() = nextImage

    private var slideShowRunning: Boolean = false

    private var startSlideShowEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val startSlideShowEnabledLiveData: LiveData<Boolean> get() = startSlideShowEnabled

    private var stopSlideShowEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val stopSlideShowEnabledLiveData: LiveData<Boolean> get() = stopSlideShowEnabled

    init {
        slideShowTask = java.lang.Runnable {
            if (slideShowRunning) {
                nextImage.postValue(Unit)
                slideShowHandler.postDelayed(slideShowTask, TimeUnit.MILLISECONDS.convert(slideShowDelaySeconds, TimeUnit.SECONDS))
            }
        }
    }

    fun startSlideshow() {
        if(!slideShowRunning) {
            startSlideShowEnabled.postValue(false)
            stopSlideShowEnabled.postValue(true)
            slideShowRunning = true
            slideShowHandler.postDelayed(slideShowTask, TimeUnit.MILLISECONDS.convert(slideShowDelaySeconds, TimeUnit.SECONDS))
        }
    }

    fun stopSlideShow() {
        slideShowRunning = false
        slideShowHandler.removeCallbacks(slideShowTask)
        startSlideShowEnabled.postValue(true)
        stopSlideShowEnabled.postValue(false)
    }

    override fun onCleared() {
        slideShowHandler.removeCallbacks(slideShowTask)
    }

    /**
     * Handle pause-specific events
     */
    fun onPause() {
        stopSlideShow()
    }

    fun setSlideshowDelaySeconds(delay: Long) {
        slideShowDelaySeconds = delay
    }

}