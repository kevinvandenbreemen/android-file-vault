package com.vandenbreemen.mobilesecurestorage.android.mvp.fileselect;

import java.io.File;
import java.util.List;

/**
 * <h2>Intro
 * <p>
 * <h2>Other Details
 *
 * @author kevin
 */
public interface FileSelectView {

    /**
     * Display all files
     *
     * @param files
     */
    void listFiles(List<File> files);

    /**
     * When file has been selected/confirmed
     *
     * @param selected
     */
    void select(File selected);
}
