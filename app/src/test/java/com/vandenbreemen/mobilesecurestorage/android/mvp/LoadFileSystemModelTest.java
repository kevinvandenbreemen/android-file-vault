package com.vandenbreemen.mobilesecurestorage.android.mvp;

import android.os.Environment;

import com.vandenbreemen.mobilesecurestorage.android.mvp.loadfilesystem.LoadFileSystemModel;
import com.vandenbreemen.mobilesecurestorage.security.SecureString;
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.spongycastle.util.encoders.Base64;

import java.io.File;

import static junit.framework.TestCase.assertTrue;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
@RunWith(RobolectricTestRunner.class)
public class LoadFileSystemModelTest {

    @Test
    public void sanityTestLoad() throws Exception {

        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "test");

        String password = "password";
        new SecureFileSystem(file) {
            @Override
            protected SecureString getPassword() {
                return SecureFileSystem.generatePassword(new SecureString(Base64.encode(password.getBytes())));
            }
        };

        LoadFileSystemModel model = new LoadFileSystemModel(file);
        model.providePassword(password);

    }

    @Test
    public void testReturnsCredentials() throws Exception {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "test");

        String password = "password";
        new SecureFileSystem(file) {
            @Override
            protected SecureString getPassword() {
                return SecureFileSystem.generatePassword(new SecureString(Base64.encode(password.getBytes())));
            }
        };

        LoadFileSystemModel model = new LoadFileSystemModel(file);

        assertTrue("Password match",
                SecureFileSystem.generatePassword(
                        new SecureString(Base64.encode(password.getBytes()))).equals(model.providePassword(password).getPassword()));
    }

}
