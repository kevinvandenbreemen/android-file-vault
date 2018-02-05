package com.vandenbreemen.mobilesecurestorage.android.task;

import android.os.AsyncTask;

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials;
import com.vandenbreemen.mobilesecurestorage.log.SystemLog;
import com.vandenbreemen.mobilesecurestorage.security.SecureString;
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem;

/**
 * <h2>Intro
 * <p>Asynchronous loading of an SFS
 * <h2>Other Details
 *
 * @author kevin
 */
public class SFSAccessTask extends AsyncTask<SFSCredentials, Void, AsyncResult<SecureFileSystem>> {

    @Override
    protected AsyncResult<SecureFileSystem> doInBackground(SFSCredentials... sfsCredentials) {

        SFSCredentials credentials = sfsCredentials[0];

        //  Make a copy as the credentials will likely get garbage collected (destroying the password)
        SecureString password = SecureFileSystem.copyPassword(credentials.getPassword());

        try{
            return new AsyncResult<SecureFileSystem>(new SecureFileSystem(sfsCredentials[0].getFileLocation()){
                @Override
                protected SecureString getPassword() {
                    return password;
                }
            });
        }catch(Exception ex){
            SystemLog.get().error("Failed to load SFS at {}", ex, credentials.getFileLocation());
            return new AsyncResult<SecureFileSystem>(ex);
        }
    }


}
