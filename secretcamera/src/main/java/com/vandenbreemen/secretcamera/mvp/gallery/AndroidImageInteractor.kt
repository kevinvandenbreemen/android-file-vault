package com.vandenbreemen.secretcamera.mvp.gallery

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.schedulers.Schedulers.computation

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
class AndroidImageInteractor {
    fun convertByteArrayToBitmap(imageBytes: ByteArray): Single<Bitmap> {
        return Single.create(SingleOnSubscribe<Bitmap> {
            val ret = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            it.onSuccess(ret)
        }).subscribeOn(computation())
    }


}