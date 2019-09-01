package com.vandenbreemen.mobilesecurestorage.patterns.mvp

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.log.SystemLog
import com.vandenbreemen.mobilesecurestorage.log.d
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File

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
 * A view that knows how to resume the app when the user returns
 */
interface  Pausable {

    fun pauseWithFileOpen(fileLocation: File)

}

/**
 * What every presenter should provide
 */
interface PresenterContract {
    fun start()
    fun close()

    /**
     * Pause work.  This method will cause the program to resume when the user returns and enters their
     * SFS password.  If the UI is not capable of pausing this method will simply close the presenter and exit.
     */
    fun pause()
    fun isClosed(): Boolean
}

abstract class Presenter<out M : Model, out V : View>(private val model: M, private val view: V) : PresenterContract {

    private val disposal: CompositeDisposable = CompositeDisposable()

    private var isPaused: Boolean = false

    override fun start() {

        if(isPaused) {
            SystemLog.get().d("Presenter", "Presenter is paused.  Will not attempt to start again.")
            return
        }

        model.init().subscribe(
                {
                    view.onReadyToUse()
                    this.setupView()
                },
                { e -> view.showError(ApplicationError("Unexpected error:  ${e.localizedMessage}")) }
        )
    }

    /**
     * Once the view is #onReadyToUse, call any API on the view required for it to initialize further details etc.  For
     * example this could be adding a title to a screen based on the title on an accessed record.  Do NOT call view.readyToUse() from
     * this method!
     */
    protected abstract fun setupView()

    override fun close() {
        model.close()
        disposal.dispose()
    }

    override fun pause() {

        if (view is Pausable && !model.isClosed()) {

            val fileLocation = model.copyCredentials().fileLocation

            try {
                view.pauseWithFileOpen(fileLocation)
            } catch (exception: Exception) {
                close() //  If pause fails then close
                SystemLog.get().error(javaClass.simpleName, "Failed to pause SFS", exception)
                return
            }

            isPaused = true

            model.close()
            disposal.dispose()

            return
        }

        close()

    }

    override fun isClosed(): Boolean {
        return model.isClosed()
    }

    /**
     * Ensure that on closing of this presenter the given disposable will be disposed of
     */
    fun addForDisposal(disposable: Disposable) {
        disposal.add(disposable)
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
        if (isClosed()) {
            return
        }
        credentials.finalize()
        sfs.close()
        onClose()
    }

    /**
     * Any additional logic you'd like to perform after the model has been closed
     */
    abstract fun onClose()

    fun copyCredentials(): SFSCredentials {
        return credentials.copy()
    }

    fun isClosed(): Boolean {
        return credentials.finalized()
    }

    /**
     * Do any setup necessary for the model to work.  This method is called once the SFS has been initialized
     */
    protected abstract fun setup()
}