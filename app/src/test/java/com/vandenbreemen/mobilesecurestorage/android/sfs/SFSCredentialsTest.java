package com.vandenbreemen.mobilesecurestorage.android.sfs;

import android.os.Environment;
import android.os.Parcel;

import com.vandenbreemen.mobilesecurestorage.security.SecureString;
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
@RunWith(RobolectricTestRunner.class)
public class SFSCredentialsTest {

    private File file;

    private SecureString password;

    @Before
    public void setup() throws Exception {
        file = new File(Environment.getExternalStorageDirectory() + File.separator + "test");
        file.createNewFile();

        //  Basic password
        password = new SecureString("test".getBytes());
    }

    @Test
    public void sanityTestFile() {
        SFSCredentials credentials = new SFSCredentials(file, password);

        Parcel parcel = Parcel.obtain();

        credentials.writeToParcel(parcel, credentials.describeContents());
        parcel.setDataPosition(0);

        SFSCredentials read = SFSCredentials.CREATOR.createFromParcel(parcel);
        File readFile = read.getFileLocation();

        assertThat(readFile, allOf(
                notNullValue(),
                is(file)
        ));
    }

    @Test
    public void sanityTestPassword() {
        SFSCredentials credentials = new SFSCredentials(file, password);

        Parcel parcel = Parcel.obtain();

        credentials.writeToParcel(parcel, credentials.describeContents());
        parcel.setDataPosition(0);

        SFSCredentials read = SFSCredentials.CREATOR.createFromParcel(parcel);
        SecureString readPassword = read.getPassword();

        assertThat(readPassword, allOf(
                notNullValue(),
                is(password)
        ));
    }

    @Test
    public void testKeyset() {

        password = SecureFileSystem.generatePassword(password);

        SFSCredentials credentials = new SFSCredentials(file, password);

        Parcel parcel = Parcel.obtain();

        credentials.writeToParcel(parcel, credentials.describeContents());
        parcel.setDataPosition(0);

        SFSCredentials read = SFSCredentials.CREATOR.createFromParcel(parcel);
        SecureString readPassword = read.getPassword();

        assertTrue("Keyset match", password.equals(readPassword));
    }

}
