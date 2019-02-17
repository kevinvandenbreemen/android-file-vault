package com.vandenbreemen.mobilesecurestorage.file.api;

import com.vandenbreemen.mobilesecurestorage.file.FileMeta;

import java.io.Serializable;

/**
 * @author kevin
 */
public class FileDetails implements Serializable {

    private static final long serialVersionUID = 6727627730465858239L;

    private FileMeta fileMeta;

    public FileDetails() {
    }

    public FileMeta getFileMeta() {
        return fileMeta;
    }


    public void setFileMeta(FileMeta fileMeta) {
        this.fileMeta = fileMeta;
    }

    public FileType getFileType() {
        return fileMeta.getFileType();
    }
}
