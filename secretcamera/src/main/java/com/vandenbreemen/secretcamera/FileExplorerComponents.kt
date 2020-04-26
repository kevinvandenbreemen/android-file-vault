package com.vandenbreemen.secretcamera

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions.FileListItemView
import com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions.FileTypeIcon

class FileTypeIconDrawableProvider(private val context: Context) {

    fun getDrawableFor(fileType: FileTypeIcon): Drawable? {
        return context.getDrawable(com.vandenbreemen.mobilesecurestorage.R.drawable.ic_icon_unknown)
    }

}

class FileItemViewHolder(val group: ViewGroup) : RecyclerView.ViewHolder(group)

/**
 *
 * @author kevin
 */
class ListFilesAdapter(private val files: List<FileListItemView>, private val drawableProvider: FileTypeIconDrawableProvider) : RecyclerView.Adapter<FileItemViewHolder>() {
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
    }

}