package com.vandenbreemen.secretcamera.robot

import android.app.Activity
import android.widget.Button
import android.widget.EditText
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.security.crypto.extListFiles
import com.vandenbreemen.secretcamera.*
import com.vandenbreemen.secretcamera.mvp.impl.TakeNewNoteModel
import junit.framework.TestCase
import org.robolectric.Robolectric
import org.robolectric.Shadows

/**
 * Created by kevin on 24/03/18.
 */
class NoteDetailsRobot() : BaseRobot(NoteDetailsActivity::class.java) {

    private lateinit var activity: NoteDetailsActivity

    fun createNote(noteTitle:String, noteContent:String){
        TakeNewNoteModel.storeNote(sfs(), noteTitle, noteContent)
    }

    fun startActivity():NoteDetailsActivity{
        val noteFile = sfs().extListFiles()[0]
        val selection = StringSelection(noteFile, credentials())

        val intent = intent()

        intent.putExtra(SELECTED_STRING, selection)
        activity = Robolectric.buildActivity(NoteDetailsActivity::class.java, intent)
                .create()
                .resume()
                .get()

        return activity
    }

    fun checkWentToActivity(activity: Activity, clazz: Class<out Activity>){
        val shadow = Shadows.shadowOf(activity)
        val intent = shadow.nextStartedActivity
        val shadowIntent = Shadows.shadowOf(intent)
        TestCase.assertEquals("Go to ${clazz.simpleName}", clazz, shadowIntent.intentClass)
        TestCase.assertNotNull("Credentials", intent.getParcelableExtra(SFSCredentials.PARM_CREDENTIALS))
    }

    fun clickOkay(activity: Activity){
        activity.findViewById<Button>(R.id.ok).performClick()
    }

    fun checkTitle(title: String) {
        TestCase.assertEquals("Note title", title, activity.findViewById<EditText>(R.id.title).text.toString())

    }

    fun checkContent(content: String) {
        TestCase.assertEquals("Note content", content, activity.findViewById<EditText>(R.id.content).text.toString())
    }

}