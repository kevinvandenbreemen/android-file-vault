package com.vandenbreemen.mobilesecurestorage.android

import android.support.test.runner.AndroidJUnit4
import android.util.Log
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import junit.framework.TestCase.*
import org.awaitility.Awaitility.await
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class RxAndroidLearningTest {

    companion object {

        const val TAG = "RXAndroidLearningTest"
    }

    @Test
    fun howToEnsureRunsOnIO(){
        var whatThread:Long? = null
        Single.create(SingleOnSubscribe<Unit> {
            whatThread = Thread.currentThread().id
            it.onSuccess(Unit)
        }).subscribeOn(io()).observeOn(mainThread()).subscribe(
                {unit->}, {error->
            error.printStackTrace()
            fail("Error occurred")}
        )

        await().atMost(1, TimeUnit.SECONDS).until { whatThread?:-1 >= 0L }
        Log.d(TAG, "single exec thread = $whatThread")
        assertNotSame("Main thread", 2L, whatThread!!)
    }

    @Test
    fun howToEnsureConsumesOnMainThread(){
        var whatThread:Long? = null
        Single.create(SingleOnSubscribe<Unit> {
            it.onSuccess(Unit)
        }).subscribeOn(io()).observeOn(mainThread()).subscribe(
                {unit->whatThread = Thread.currentThread().id}, {error->
            error.printStackTrace()
            fail("Error occurred")}
        )

        await().atMost(1, TimeUnit.SECONDS).until { whatThread?:-1 >= 0L }
        Log.d(TAG, "single exec thread = $whatThread")
        assertEquals("Main thread", 2L, whatThread!!)
    }

}