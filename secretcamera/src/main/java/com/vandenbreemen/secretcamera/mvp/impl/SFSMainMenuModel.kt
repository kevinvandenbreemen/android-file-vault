package com.vandenbreemen.secretcamera.mvp.impl

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.log.SystemLog
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import com.vandenbreemen.mobilesecurestorage.security.crypto.extListFiles
import com.vandenbreemen.mobilesecurestorage.security.crypto.getFileMeta
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.secretcamera.mvp.gallery.PicturesFileTypes
import com.vandenbreemen.secretcamera.mvp.impl.projects.ProjectFileTypes
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.computation

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
class SFSMainMenuModel( val credentials: SFSCredentials):Model(credentials) {
    override fun setup() {
        SystemLog.get().debug("SFSMainMenu Engaged.  The following file type collections available: " +
                "${NoteFileTypes.values()}, ${PicturesFileTypes.values()}, ${ProjectFileTypes.values()}")
    }

    override fun onClose() {

    }

    fun getNotes(): Single<List<String>> {
        return Single.create(SingleOnSubscribe<List<String>> {
            it.onSuccess(sfs.extListFiles().filter {
                NoteFileTypes.SIMPLE_NOTE.equals(sfs.getFileMeta(it)?.getFileType() ?: null)
            })
        }).subscribeOn(computation()).observeOn(mainThread())
    }

    fun getSFS():SecureFileSystem = sfs

}