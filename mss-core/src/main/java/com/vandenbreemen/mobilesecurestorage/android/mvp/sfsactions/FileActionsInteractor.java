package com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions;

import com.vandenbreemen.mobilesecurestorage.message.ApplicationError;
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Interactor for executing tasks on a single file
 *
 * @author kevin
 */
public class FileActionsInteractor {

    private SecureFileSystem sfs;

    /**
     * Name of the file you're working with
     */
    private String fileName;

    public FileActionsInteractor(SecureFileSystem sfs, String fileToWorkWith) {
        this.sfs = sfs;
        this.fileName = fileToWorkWith;
    }

    /**
     * Renames the file to the new filename
     *
     * @param newFileName
     * @return The new filename if rename successful
     * @throws ApplicationError If the name chosen is invalid or already exists
     */
    public String rename(@NotNull String newFileName) throws ApplicationError {

        if (StringUtils.isBlank(newFileName)) {
            throw new ApplicationError("Please specify a new filename");
        }

        if (sfs.exists(newFileName)) {
            throw new ApplicationError("Cannot rename to '" + newFileName + "'; file already exists.");
        }

        sfs.rename(fileName, newFileName);
        this.fileName = newFileName;
        return this.fileName;
    }
}
