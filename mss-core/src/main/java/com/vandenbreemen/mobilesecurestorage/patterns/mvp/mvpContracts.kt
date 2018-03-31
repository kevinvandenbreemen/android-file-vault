package com.vandenbreemen.mobilesecurestorage.patterns.mvp

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.log.SystemLog
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/*
General contracts for anything implementing MVP pattern
 */

interface View{

    /**
     * Signify that the system is ready to use
     */
    fun onReadyToUse()

    /**
     * Display the given error
     */
    fun showError(error: ApplicationError)

}

/**
 * What every presenter should provide
 */
interface PresenterContract {
    fun start()
    fun close()
}

abstract class Presenter<out M : Model, out V : View>(private val model: M, private val view: V) : PresenterContract {

    override fun start() {
        model.init().subscribe(
                {
                    view.onReadyToUse()
                    this.setupView()
                },
                { e -> view.showError(ApplicationError("Unexpected error:  ${e.localizedMessage}")) }
        )
    }

    protected abstract fun setupView()

    override fun close() {
        model.close()
    }

}

abstract class Model(private val credentials: SFSCredentials) {

    protected lateinit var sfs:SecureFileSystem

    fun init(): Single<Unit>{
        return Single.create(SingleOnSubscribe<Unit> {
            try {
                this.sfs = object : SecureFileSystem(credentials.fileLocation) {
                    override fun getPassword(): SecureString? {
                        return credentials.password
                    }
                }

                this.setup()

                it.onSuccess(Unit)
            } catch (exception: Exception) {
                SystemLog.get().error(javaClass.simpleName, "Failed to load SFS", exception)
                it.onError(exception)
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
    }

    fun close() {
        credentials.finalize()
        sfs.close()
        onClose()
    }

    /**
     * Any additional logic you'd like to perform after the model has been closed
     */
    protected abstract fun onClose()

    fun copyCredentials(): SFSCredentials {
        return credentials.copy()
    }

    /**
     * Do any setup necessary for the model to work
     */
    protected abstract fun setup()
}