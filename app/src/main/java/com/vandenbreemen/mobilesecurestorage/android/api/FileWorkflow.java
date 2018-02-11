package com.vandenbreemen.mobilesecurestorage.android.api;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class FileWorkflow implements Parcelable {

    public static final Creator<FileWorkflow> CREATOR = new Creator<FileWorkflow>() {
        @Override
        public FileWorkflow createFromParcel(Parcel source) {
            FileWorkflow ret = new FileWorkflow();
            ret.fileOrDirectory = new File(source.readString());
            return ret;
        }

        @Override
        public FileWorkflow[] newArray(int size) {
            return new FileWorkflow[0];
        }
    };

    /**
     * Name of workflow for moving this around
     */
    public static final String PARM_WORKFLOW_NAME = "FileWorkflow";
    private File fileOrDirectory;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileOrDirectory.getAbsolutePath());
    }

    public File getFileOrDirectory() {
        return fileOrDirectory;
    }

    public void setFileOrDirectory(File fileOrDirectory) {
        this.fileOrDirectory = fileOrDirectory;
    }
}
