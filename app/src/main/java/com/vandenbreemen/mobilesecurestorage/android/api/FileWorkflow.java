package com.vandenbreemen.mobilesecurestorage.android.api;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

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

            String className = source.readString();
            try {
                ret.targetActivity = (Class<? extends Activity>) Class.forName(className);
            } catch (Exception ex) {
                Log.e("FileWorkflowError", "Failed to get target activity class", ex);
            }

            String filePath = source.readString();
            if (!StringUtils.isBlank(filePath)) {
                ret.fileOrDirectory = new File(filePath);
            }

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
    private Class<? extends Activity> targetActivity;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.targetActivity != null ? this.targetActivity.getName() : "");
        dest.writeString(this.fileOrDirectory != null ? fileOrDirectory.getAbsolutePath() : "");
    }

    public File getFileOrDirectory() {
        return fileOrDirectory;
    }

    public void setFileOrDirectory(File fileOrDirectory) {
        this.fileOrDirectory = fileOrDirectory;
    }

    public Class<? extends Activity> getTargetActivity() {
        return targetActivity;
    }

    public void setTargetActivity(Class<? extends Activity> targetActivity) {
        this.targetActivity = targetActivity;
    }
}
