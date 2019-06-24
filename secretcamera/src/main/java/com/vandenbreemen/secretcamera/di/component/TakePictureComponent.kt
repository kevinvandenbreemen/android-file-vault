package com.vandenbreemen.secretcamera.di.component

import com.vandenbreemen.secretcamera.TakePictureActivity
import com.vandenbreemen.secretcamera.di.activity.TakePictureActivityModule
import com.vandenbreemen.secretcamera.di.mvp.TakePicturePresenterModule
import dagger.Component

@Component(modules = [TakePictureActivityModule::class, TakePicturePresenterModule::class])
interface TakePictureComponent {

    @Component.Factory
    interface Factory {
        fun create(takePictureActivityModule: TakePictureActivityModule, takePicturePresenterModule: TakePicturePresenterModule): TakePictureComponent
    }

    fun inject(activity: TakePictureActivity)

}