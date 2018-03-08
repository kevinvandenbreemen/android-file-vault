package com.vandenbreemen.mobilesecurestorage.android.mvp.createfilesystem;

import com.vandenbreemen.mobilesecurestorage.android.api.ErrorDisplay;
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials;

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
     * @param credentials - credentials for newly created SFS
     */
    void onComplete(SFSCredentials credentials);

}
