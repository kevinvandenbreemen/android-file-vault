package com.vandenbreemen.secretcamera.mvp.takepicture

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.ImportedFileData
import com.vandenbreemen.mobilesecurestorage.file.api.SecureFileSystemInteractor
import com.vandenbreemen.mobilesecurestorage.file.api.getSecureFileSystemInteractor
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.DateInteractor
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import com.vandenbreemen.secretcamera.mvp.gallery.PicturesFileTypes
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.schedulers.Schedulers.computation
import java.text.SimpleDateFormat

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
class TakePictureModel(val dateInteractor: DateInteractor, sfsCredentials: SFSCredentials) : Model(sfsCredentials) {

    companion object {
        const val FORMAT = "yyyyMMddHHmmss"
    }

    override fun onClose() {

    }

    private lateinit var secureFileSystemInteractor: SecureFileSystemInteractor

    override fun setup() {
        this.secureFileSystemInteractor = getSecureFileSystemInteractor(sfs)
    }

    fun storePicture(pictureBytes: ByteArray): Single<Unit> {
        return Single.create<Unit>(SingleOnSubscribe { emitter ->
            val fileData = ImportedFileData(pictureBytes)
            secureFileSystemInteractor.importToFile(fileData, getFilename(), PicturesFileTypes.CAPTURED_IMAGE)
            emitter.onSuccess(Unit)
        }).subscribeOn(computation()).observeOn(computation())
    }

    private fun getFilename(): String {
        val filePrefix = "CAP_${SimpleDateFormat(FORMAT).format(dateInteractor.getDateTime().time)}"
        var filename = filePrefix

        var suffix = 0
        while (sfs.exists(filename)) {
            suffix++
            filename = "${filePrefix}_$suffix"
        }

        return filename
    }


}