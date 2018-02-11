package com.vandenbreemen.mobilesecurestorage.android.mvp.createfilesystem;

import com.vandenbreemen.mobilesecurestorage.android.api.ErrorDisplay;
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public interface CreateSecureFileSystemView extends ErrorDisplay {

    /**
     * Completed creation
     *
     * @param sfs
     */
    void onComplete(SecureFileSystem sfs);

}
