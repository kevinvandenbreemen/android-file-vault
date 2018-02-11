package com.vandenbreemen.mobilesecurestorage.android;

import android.content.Intent;
import android.os.Environment;

import com.vandenbreemen.mobilesecurestorage.android.api.FileWorkflow;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.io.File;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
@RunWith(RobolectricTestRunner.class)
public class CreateSecureFileSystemFunctionalTest {

    private File directory;

    @Before
    public void setup() {
        this.directory = new File(Environment.getExternalStorageDirectory() + File.separator + "dir");
        directory.mkdir();
    }

    @Test
    public void sanityTestStartActivity() {

        FileSelectActivity activity = Robolectric.setupActivity(FileSelectActivity.class);

        FileWorkflow workflow = new FileWorkflow();
        workflow.setFileOrDirectory(directory);

        Intent startCreateSFS = new Intent(activity, CreateSecureFileSystem.class);
        startCreateSFS.putExtra("Workflow", workflow);

        CreateSecureFileSystem createSecureFileSystem = Robolectric.buildActivity(CreateSecureFileSystem.class, startCreateSFS).get();



    }

}
