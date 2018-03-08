package com.vandenbreemen.mobilesecurestorage.android.task;

import com.vandenbreemen.mobilesecurestorage.TestConstants;
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials;
import com.vandenbreemen.mobilesecurestorage.security.SecureString;
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

/**
 * <h2>Intro
 * <p>Validate behavior of SFS task
 * <h2>Other Details
 *
 * @author kevin
 */
@RunWith(RobolectricTestRunner.class)
public class SFSAccessTaskTest {

    private File sfsLocation;

    private String password = "fishDish";

    private ArrayList<String> expectedList;

    @Before
    public void setup() throws Exception{
        this.expectedList = new ArrayList<String>(Arrays.asList("Larry", "Kermit", "Frog"));
        this.sfsLocation = TestConstants.getTestFile("sfsAccess_"+System.currentTimeMillis());

        //  Stand up a Secure File System here
        SecureString passwd = SecureFileSystem.generatePassword(new SecureString(password.getBytes()));
        SecureFileSystem sfs = new SecureFileSystem(sfsLocation) {
            @Override
            protected SecureString getPassword() {
                return passwd;
            }
        };

        sfs.storeObject("testFile", expectedList);
    }

    @Test
    public void testAccessSFS() throws Exception{
        SFSCredentials credentials = new SFSCredentials(sfsLocation, SecureFileSystem.generatePassword(new SecureString(password.getBytes())));
        SFSAccessTask task = new SFSAccessTask();

        task.execute(new SFSCredentials[]{credentials});

        Robolectric.flushBackgroundThreadScheduler();

        AsyncResult<SecureFileSystem> result = task.get();

        assertFalse("Unexpected error", result.getError().isPresent());
        assertNotNull("SFS", result.getResult());

    }

    @Test
    public void testRaiseError() throws Exception{
        SFSCredentials credentials = new SFSCredentials(sfsLocation, SecureFileSystem.generatePassword(new SecureString("wrongPass".getBytes())));
        SFSAccessTask task = new SFSAccessTask();

        task.execute(new SFSCredentials[]{credentials});

        Robolectric.flushBackgroundThreadScheduler();

        AsyncResult<SecureFileSystem> result = task.get();

        assertTrue("expected error", result.getError().isPresent());
        assertNull("SFS", result.getResult());
    }

    @Test
    public void testLoadFiles() throws Exception{
        SFSCredentials credentials = new SFSCredentials(sfsLocation, SecureFileSystem.generatePassword(new SecureString(password.getBytes())));
        SFSAccessTask task = new SFSAccessTask();

        task.execute(new SFSCredentials[]{credentials});

        Robolectric.flushBackgroundThreadScheduler();

        AsyncResult<SecureFileSystem> result = task.get();

        assertEquals("File list", 1, result.getResult().listFiles().size());
        List<String> list = (List<String>) result.getResult().loadFile("testFile");

        assertEquals("List", expectedList, list);

    }
}
