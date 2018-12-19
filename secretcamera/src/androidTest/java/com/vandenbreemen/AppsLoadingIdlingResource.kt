package com.vandenbreemen

import android.support.test.espresso.IdlingResource

/**
 * Simple custom idling resource to force tests to wait for screen transitions to actually finish!
 */
class AppsLoadingIdlingResource : IdlingResource {

    val transitionCallbacks = mutableListOf<IdlingResource.ResourceCallback>()

    var loadingNow = false

    override fun getName() = "Screen is Loading"

    override fun isIdleNow(): Boolean {
        return !loadingNow
    }

    fun startLoading() {
        loadingNow = true
    }

    fun doneLoading() {
        loadingNow = false
        transitionCallbacks.forEach{it -> it.onTransitionToIdle()}
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        callback ?. let { it
            transitionCallbacks.add(it)
        }
    }
}