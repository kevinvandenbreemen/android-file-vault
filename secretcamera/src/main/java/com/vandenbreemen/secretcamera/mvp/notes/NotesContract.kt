package com.vandenbreemen.secretcamera.mvp.notes

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.PresenterContract
import com.vandenbreemen.mobilesecurestorage.patterns.mvp.View
import com.vandenbreemen.secretcamera.api.Note
import io.reactivex.Single

/**
 * <h2>Intro</h2>
 *
 * <h2>Other Details</h2>
 * @author kevin
 */
interface TakeNewNotePresenter : PresenterContract {

    fun provideNoteDetails(title: String?, note: String?)

    fun onCancel()
    fun saveAndClose(noteTitle: String?, noteContent: String?): Single<Unit>

}

interface TakeNewNoteView : View {

    fun onNoteSucceeded(message: String)

    fun close(credentials: SFSCredentials)
}

interface NoteDetailsPresenter:PresenterContract{

    fun onOk()

    fun onEdit()

}

interface NoteDetailsView:View{
    fun setNoteTitle(title:String)
    fun setNoteContent(content:String)
    fun close(credentials: SFSCredentials)
    fun enableEdit()
    fun getNoteOnUI(): Note
}