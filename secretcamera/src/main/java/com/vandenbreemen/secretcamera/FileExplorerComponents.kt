package com.vandenbreemen.secretcamera

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions.*
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.secretcamera.mvp.gallery.PictureFileIcons

class FileTypeIconDrawableProvider(private val context: Context) {

    fun getDrawableFor(fileType: FileTypeIcon): Drawable? {
        if (fileType == CoreFileTypeIcons.DATA) {
            return context.getDrawable(com.vandenbreemen.mobilesecurestorage.R.drawable.ic_icon_datafile)
        } else if (fileType == CoreFileTypeIcons.SYSTEM) {
            return context.getDrawable(com.vandenbreemen.mobilesecurestorage.R.drawable.ic_icon_systemfile)
        } else if (fileType == PictureFileIcons.IMAGE) {
            return context.getDrawable(R.drawable.ic_icon_picture)
        }
        return context.getDrawable(com.vandenbreemen.mobilesecurestorage.R.drawable.ic_icon_unknown)
    }

}

class FileItemViewHolder(val group: ViewGroup) : RecyclerView.ViewHolder(group)

/**
 *
 * @author kevin
 */
class ListFilesAdapter(private val sfsActionsPresenter: SFSActionsPresenter, private val files: List<FileListItemView>, private val drawableProvider: FileTypeIconDrawableProvider) : RecyclerView.Adapter<FileItemViewHolder>() {

    private var fileActionsPresenter: FileActionsPresenter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileItemViewHolder {
        val container = LayoutInflater.from(parent.context).inflate(R.layout.layout_sfs_item, parent, false) as ViewGroup

        return FileItemViewHolder(container)
    }

    override fun getItemCount(): Int {
        return files.count()
    }

    override fun onBindViewHolder(holder: FileItemViewHolder, position: Int) {
        val file = files[position]
        holder.group.findViewById<TextView>(R.id.fileName).text = file.name

        file.icon?.let { icon ->
            drawableProvider.getDrawableFor(icon)?.let { drawable ->
                holder.group.findViewById<ImageView>(R.id.fileTypeIcon).setImageDrawable(drawable)
            }

        }

        holder.group.setOnLongClickListener { view ->

            //  Create dialog with actions

            val builder = AlertDialog.Builder(view.context)
            val fileActionsView = LayoutInflater.from(view.context).inflate(com.vandenbreemen.mobilesecurestorage.R.layout.layout_file_actions, null)

            fileActionsView.findViewById<Button>(R.id.renameButton).setOnClickListener {
                val fileName = fileActionsView.findViewById<EditText>(R.id.renameInput).text.toString()
                fileActionsPresenter?.rename(fileName)
            }

            val renameInput = fileActionsView.findViewById<EditText>(R.id.renameInput)
            renameInput.setText(file.name)
            renameInput.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    fileActionsView.findViewById<TextView>(R.id.errorMessage).visibility = GONE
                }

            })

            builder.setView(fileActionsView)

            builder.setOnCancelListener { dialog ->
                fileActionsPresenter?.close()
                fileActionsPresenter = null
            }

            val dialogView: Dialog = builder.create()
            dialogView.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val fileActionsMVCView = object : FileActionsView {

                override fun fileRenameSuccess(newName: String) {
                    sfsActionsPresenter.listFiles()
                }

                override fun onReadyToUse() {

                }

                override fun showError(error: ApplicationError) {

                    val errorMessage = fileActionsView.findViewById<TextView>(R.id.errorMessage)

                    errorMessage.text = error.localizedMessage
                    errorMessage.visibility = VISIBLE
                }
            }

            fileActionsPresenter = sfsActionsPresenter.actionsFor(file.name, fileActionsMVCView)

            dialogView.show()

            true
        }
    }

}