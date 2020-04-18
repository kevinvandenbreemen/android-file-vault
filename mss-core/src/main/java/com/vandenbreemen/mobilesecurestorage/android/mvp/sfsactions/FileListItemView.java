package com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions;

import com.vandenbreemen.mobilesecurestorage.file.api.FileType;

/**
 * Light-weight view of a specific item in the file system
 *
 * @author kevin
 */
public class FileListItemView {

    final String name;
    final FileType fileType;

    FileListItemView(String name, FileType fileType) {
        this.name = name;
        this.fileType = fileType;
    }

}
