package com.vandenbreemen.mobilesecurestorage.android.mvp.importfiles

import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.Presenter
import java.io.File

/**
 * Created by kevin on 06/04/18.
 */
class FileImportPresenterImpl(val model: FileImportModel, val view: FileImportView) : Presenter<FileImportModel, FileImportView>(model, view), FileImportPresenter {
    override fun import(directory: File) {
        model.importDir(directory)
                .subscribe({
                    view.done(model.copyCredentials())
                }, {
                    view.showError(ApplicationError(it))
                })
    }

    override fun setupView() {

    }


}