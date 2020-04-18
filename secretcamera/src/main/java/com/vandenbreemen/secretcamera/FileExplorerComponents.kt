package com.vandenbreemen.secretcamera

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions.FileListItemView


class FileItemViewHolder(val group: ViewGroup) : RecyclerView.ViewHolder(group)

/**
 *
 * @author kevin
 */
class ListFilesAdapter(private val files: List<FileListItemView>) : RecyclerView.Adapter<FileItemViewHolder>() {
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
    }


}