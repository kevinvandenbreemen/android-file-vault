package com.vandenbreemen.mobilesecurestorage.android.mvp.importfiles

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.file.api.SecureFileSystemInteractor
import com.vandenbreemen.mobilesecurestorage.file.api.getSecureFileSystemInteractor
import com.vandenbreemen.mobilesecurestorage.file.getFileImporter
import com.vandenbreemen.mobilesecurestorage.log.SystemLog
import com.vandenbreemen.mobilesecurestorage.log.d
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.computation
import java.io.File

/**
 * Created by kevin on 04/04/18.
 */
class FileImportModel(credentials: SFSCredentials) : Model(credentials) {

    /**
     *
     */
    private val fileSystemInteractor = FileSystemInteractorImpl()

    override fun onClose() {

    }

    override fun setup() {

    }

    fun importDir(directoryToImport: File): Single<Unit> {

        val listFilesSinglke = fileSystemInteractor.listFiles(directoryToImport)
        return listFilesSinglke.map {
            files->
            files!!.forEach(
                    { fileToImport -> sfs.importFile(fileToImport)
                    })
            return@map Unit

        }
        listFilesSinglke.subscribe({s->})

    }
}