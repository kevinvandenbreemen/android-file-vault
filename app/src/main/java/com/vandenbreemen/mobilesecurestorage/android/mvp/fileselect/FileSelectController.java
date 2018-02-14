package com.vandenbreemen.mobilesecurestorage.android.mvp.fileselect;

import com.vandenbreemen.mobilesecurestorage.message.ApplicationError;

import org.jetbrains.annotations.Nullable;

import java.io.File;

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

    public void select(@Nullable File selected) {
        this.model.select(selected);
        if (selected.isDirectory()) {
            view.listFiles(model.listFiles());
        }
    }

    /**
     * Confirm file selection (for when the model is not auto-select)
     */
    public void confirm() {
        try {
            model.validateSelectedFile();
        } catch (ApplicationError apex) {
            view.display(apex);
            return;
        }
        view.select(model.getSelectedFile());
    }
}
