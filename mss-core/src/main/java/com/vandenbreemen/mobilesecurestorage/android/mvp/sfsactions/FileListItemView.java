package com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions;

import com.vandenbreemen.mobilesecurestorage.file.api.FileType;

/**
 * Light-weight view of a specific item in the file system
 *
 * @author kevin
 */
public class FileListItemView {

    public final String name;
    final FileType fileType;
    private FileTypeIcon icon;

    FileListItemView(String name, FileType fileType) {
        this.name = name;
        this.fileType = fileType;
    }

    public FileTypeIcon getIcon() {
        return this.icon;
    }

    void setIcon(FileTypeIcon icon) {
        this.icon = icon;
    }

}
