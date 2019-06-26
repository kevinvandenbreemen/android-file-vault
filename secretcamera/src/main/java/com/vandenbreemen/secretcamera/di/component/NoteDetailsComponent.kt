package com.vandenbreemen.secretcamera.di.component

import com.vandenbreemen.secretcamera.NoteDetailsActivity
import com.vandenbreemen.secretcamera.di.activity.NoteDetailsActivityModule
import com.vandenbreemen.secretcamera.di.mvp.NoteDetailsPresenterModule
import dagger.Component

@Component(modules = [NoteDetailsActivityModule::class, NoteDetailsPresenterModule::class])
interface NoteDetailsComponent {

    @Component.Factory
    interface Factory {
        fun create(activity: NoteDetailsActivityModule, presenter: NoteDetailsPresenterModule): NoteDetailsComponent
    }

    fun inject(activity: NoteDetailsActivity)

}