package com.vandenbreemen.mobilesecurestorage.android.mvp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <h2>Intro
 * <p>
 * <h2>Other Details
 *
 * @author kevin
 */
public class FileSelectModel {

    /**
     * App context in which this model is running
     */
    private Context context;

    /**
     * Indicate that we're selecting a directory
     */
    private boolean isSelectDirectory;

    /**
     * Initialize new file selector
     * @param context
     */
    public FileSelectModel(Context context) {
        this.context = context;
    }

    /**
     * List the files in the currently selected directory
     * @return
     */
    public List<File> listFiles() {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            return Arrays.asList(Environment.getExternalStorageDirectory().listFiles(
                    file -> isSelectDirectory ? file.isDirectory() : true
            ));
        }
        return Collections.emptyList();
    }

    public void setSelectDirectories(boolean selectDirectoriesOnly) {
        this.isSelectDirectory = selectDirectoriesOnly;
    }
}
