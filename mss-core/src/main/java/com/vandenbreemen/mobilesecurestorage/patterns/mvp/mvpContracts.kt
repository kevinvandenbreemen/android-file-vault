package com.vandenbreemen.mobilesecurestorage.patterns.mvp

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.log.SystemLog
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/*
General contracts for anything implementing MVP pattern
 */

interface ModelContract{
    val credentials:SFSCredentials

    /**
     * How to close a model
     */
    fun close()
}

interface View{

    /**
     * Signify that the system is ready to use
     */
    fun onReadyToUse()

}

/**
 * Default presenter is one that is backed by a model of type Model
 */
interface PresenterContract<out Model:com.vandenbreemen.mobilesecurestorage.patterns.mvp.Model>{

    val model:Model

    fun close()

}

open class Presenter<out T:Model>(override val model:T):PresenterContract<T>{

    override fun close() {
        model.close()
    }

}

open class Model(override val credentials:SFSCredentials):ModelContract{

    protected lateinit var sfs:SecureFileSystem

    fun init(): Single<Unit>{
        return Single.create(SingleOnSubscribe<Unit> {
            try {
                sfs = object : SecureFileSystem(credentials.fileLocation) {
                    override fun getPassword(): SecureString {
                        return credentials.password
                    }
                }
                it.onSuccess(Unit)
            } catch (exception: Exception) {
                SystemLog.get().error(javaClass.simpleName, "Failed to load SFS", exception)
                it.onError(exception)
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    override fun close() {
        credentials.finalize()
    }
}