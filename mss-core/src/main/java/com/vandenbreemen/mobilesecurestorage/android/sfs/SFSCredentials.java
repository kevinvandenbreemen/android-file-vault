package com.vandenbreemen.mobilesecurestorage.android.sfs;

import android.os.Parcel;
import android.os.Parcelable;

import com.vandenbreemen.mobilesecurestorage.security.SecureString;

import java.io.File;

/**
 * <h2>Intro
 * <p>Credentials you can use in accessing an SFS
 * <h2>Other Details
 *
 * @author kevin
 */
public class SFSCredentials implements Parcelable{

    /**
     * Name of parcelable extra
     */
    public static final String PARM_CREDENTIALS = "__CRED";
    public static final Creator<SFSCredentials> CREATOR = new Creator<SFSCredentials>() {
        @Override
        public SFSCredentials createFromParcel(Parcel in) {
            return new SFSCredentials(in);
        }

        @Override
        public SFSCredentials[] newArray(int size) {
            return new SFSCredentials[size];
        }
    };
    /**
     * Location of the file the SFS resides in
     */
    private File fileLocation;
    /**
     * Password for the SFS
     */
    private SecureString password;

    protected SFSCredentials(Parcel in) {
        String fileLocTmp = in.readString();
        fileLocation = new File(fileLocTmp);
        this.password = (SecureString) in.readSerializable();
    }

    /**
     * Programmatic credentials generation
     *
     * @param fileLocation
     * @param password
     */
    public SFSCredentials(File fileLocation, SecureString password) {
        this.fileLocation = fileLocation;
        this.password = password;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.fileLocation.getAbsolutePath());
        parcel.writeSerializable(this.password);
    }

    public File getFileLocation() {
        return fileLocation;
    }

    public SecureString getPassword() {
        return password;
    }

    public SFSCredentials copy() {
        return new SFSCredentials(fileLocation, password.copy());
    }

    @Override
    public void finalize() {
        this.password.randomFinalize();
        this.password = null;
    }
}
