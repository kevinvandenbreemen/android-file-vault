package com.vandenbreemen.secretcamera.di.component

import com.vandenbreemen.secretcamera.SFSActionsActivity
import com.vandenbreemen.secretcamera.di.activity.SFSActionsActivityModule
import com.vandenbreemen.secretcamera.di.mvp.SFSActionsModule
import dagger.Component

@Component(modules = [SFSActionsActivityModule::class, SFSActionsModule::class])
interface SFSActionsComponent {

    @Component.Factory
    interface Factory {
        fun create(actionsActivityModule: SFSActionsActivityModule, actionsModule: SFSActionsModule): SFSActionsComponent
    }

    fun inject(activity: SFSActionsActivity)

}