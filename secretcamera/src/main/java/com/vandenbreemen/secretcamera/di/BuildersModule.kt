package com.vandenbreemen.secretcamera.di

import com.vandenbreemen.secretcamera.*
import com.vandenbreemen.secretcamera.di.mvp.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by kevin on 24/03/18.
 */
@Module
abstract class BuildersModule {

    @ContributesAndroidInjector
    abstract fun bindMainActivity():MainActivity

    @ContributesAndroidInjector(modules = [TakeNotePresenterModule::class])
    abstract fun bindTakeNoteActivity():TakeNoteActivity

    @ContributesAndroidInjector(modules = [NoteDetailsPresenterModule::class])
    abstract fun bindNoteDetailsActivity():NoteDetailsActivity

    @ContributesAndroidInjector(modules = [GalleryPresenterModule::class])
    abstract fun bindGalleryActivity(): Gallery

    @ContributesAndroidInjector(modules = [PictureViewerPresenterModule::class])
    abstract fun bindPictureViewerActivity(): PictureViewerActivity

    @ContributesAndroidInjector(modules = [TakePicturePresenterModule::class])
    abstract fun bindTakePictureActivity(): TakePictureActivity

    @ContributesAndroidInjector(modules = [ProjectManagementModule::class])
    abstract fun bindProjectManagementActivity(): ProjectsActivity

    @ContributesAndroidInjector(modules=[ProjectDetailsModule::class])
    abstract  fun bindProjectDetailsActivity(): ProjectDetailsActivity

}