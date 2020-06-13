package com.vandenbreemen.secretcamera.mvp.gallery

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import com.vandenbreemen.mobilesecurestorage.log.SystemLog
import com.vandenbreemen.mobilesecurestorage.log.e
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
    fun convertByteArrayToBitmapRX(imageBytes: ByteArray): Single<Bitmap> {
        return Single.create(SingleOnSubscribe<Bitmap> {
            try {
                val ret = convertByteArrayToBitmapSynchronous(imageBytes)
                it.onSuccess(ret)
            } catch (ex: Exception) {
                SystemLog.get().e(AndroidImageInteractor::class.java.simpleName, "Failed to convert bytes to bitmap", ex)
                it.onError(ex)
            }
        }).subscribeOn(computation())
    }

    fun convertByteArrayToBitmapSynchronous(imageBytes: ByteArray) =
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    ?: throw NullPointerException("Decoded bitmap was null")

    fun generateThumbnail(bitmap: Bitmap): Single<Bitmap> {
        return Single.create(SingleOnSubscribe<Bitmap> {
            it.onSuccess(ThumbnailUtils.extractThumbnail(bitmap, 150, 150))
        }).subscribeOn(computation())
    }

    fun generateThumbnailSynchronous(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        return ThumbnailUtils.extractThumbnail(bitmap, width, height)
    }

}