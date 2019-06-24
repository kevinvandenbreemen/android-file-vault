package com.vandenbreemen.secretcamera.di

import com.vandenbreemen.secretcamera.*
import com.vandenbreemen.secretcamera.di.activity.*
import com.vandenbreemen.secretcamera.di.component.*
import com.vandenbreemen.secretcamera.di.mvp.*


fun injectTakeNote(activity: TakeNoteActivity) {
    return DaggerTakeNoteComponent.factory().create(
            TakeNoteActivityModule(activity),
            TakeNotePresenterModule()
    ).inject(activity)
}

fun injectTakePicture(activity: TakePictureActivity) {
    DaggerTakePictureComponent.factory().create(
            TakePictureActivityModule(activity),
            TakePicturePresenterModule()
    ).inject(activity)
}

fun injectPictureViewer(activity: PictureViewerActivity) {
    DaggerPictureViewerComponent.factory().create(
            PictureViewerAcvitityModule(activity),
            PictureViewerPresenterModule()
    ).inject(activity)
}

fun injectNoteDetails(activity: NoteDetailsActivity) {
    DaggerNoteDetailsComponent.factory().create(
            NoteDetailsActivityModule(activity),
            NoteDetailsPresenterModule()
    ).inject(activity)
}

fun injectSFSActions(activity: SFSActionsActivity) {
    DaggerSFSActionsComponent.factory().create(
            SFSActionsActivityModule(activity),
            SFSActionsModule()
    ).inject(activity)
}

fun injectProjectDetails(activity: ProjectDetailsActivity) {
    DaggerProjectDetailsComponent.factory().create(
            ProjectDetailsActivityModule(activity),
            ProjectDetailsModule()
    ).inject(activity)
}

fun injectProjectsActivithy(activity: ProjectsActivity) {
    DaggerProjectsComponent.factory().create(
            ProjectManagementModule(),
            ProjectsActivityModule(activity)
    ).inject(activity)
}

fun injectGallery(activity: Gallery) {
    DaggerGalleryComponent.factory().create(
            GalleryActivityModule(activity),
            GalleryPresenterModule()
    ).inject(activity)
}