package com.vandenbreemen.mobilesecurestorage.android.mvp;

/**
 * <h2>Intro
 * <p>
 * <h2>Other Details
 *
 * @author kevin
 */
public class FileSelectController {

    private FileSelectModel model;

    private FileSelectView view;

    public FileSelectController(FileSelectModel model, FileSelectView view) {
        this.model = model;
        this.view = view;
    }

    public void start() {
        this.view.listFiles(model.listFiles());
    }
}
