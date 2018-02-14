package com.vandenbreemen.mobilesecurestorage.android.mvp.fileselect;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

import com.vandenbreemen.mobilesecurestorage.message.ApplicationError;

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
     * Current selected directory (present working dir)
     */
    private File pwd;
    /**
     * Listener for file selection
     */
    private FileSelectListener listener;
    private boolean autoSelect;
    private File selected;

    /**
     * Initialize new file selector
     * @param context
     */
    public FileSelectModel(Context context) {
        this.context = context;
        this.autoSelect = true;
    }

    /**
     * Set listener for when user selects a file
     *
     * @param listener
     */
    public void setListener(FileSelectListener listener) {
        this.listener = listener;
    }

    /**
     * List the files in the currently selected directory
     * @return
     */
    public List<File> listFiles() {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            File thePwd = pwd != null ? pwd : Environment.getExternalStorageDirectory();
            return Arrays.asList(thePwd.listFiles(
                    file -> isSelectDirectory ? file.isDirectory() : true
            ));
        }
        return Collections.emptyList();
    }

    public void setSelectDirectories(boolean selectDirectoriesOnly) {
        this.isSelectDirectory = selectDirectoriesOnly;
    }

    /**
     * Select the given file/directory
     * @param file
     */
    public void select(File file) {
        if(file.isDirectory()){
            this.pwd = file;
            if (isSelectDirectory) {
                this.selected = file;
            }
        } else if (listener != null && autoSelect) {
            listener.onSelectFile(file);
        } else {
            this.selected = file;
        }
    }

    /**
     * Whether selection of a file will trigger app to continue
     *
     * @return
     */
    public boolean isAutoSelect() {
        return autoSelect;
    }

    /**
     * Tell the model to automatically continue app flow when file selected.  By default
     * auto-selection IS enabled
     *
     * @param autoSelect
     */
    public void setAutoSelect(boolean autoSelect) {
        this.autoSelect = autoSelect;
    }

    public File getSelectedFile() {
        return selected;
    }

    void validateSelectedFile() throws ApplicationError {
        if (!isSelectDirectory && selected == null) {
            throw new ApplicationError("File selection is required");
        }
    }

    /**
     * Listener for selecting a file (not a directory)
     */
    public static interface FileSelectListener {

        /**
         * Actions to perform when a file has been selected
         *
         * @param file
         */
        void onSelectFile(File file);

    }
}
