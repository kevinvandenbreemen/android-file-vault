package com.vandenbreemen.mobilesecurestorage.android;

import android.content.Intent;
import android.os.Environment;
import android.widget.EditText;

import com.vandenbreemen.mobilesecurestorage.R;
import com.vandenbreemen.mobilesecurestorage.android.api.FileWorkflow;

import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.util.concurrent.TimeUnit;

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

        CreateSecureFileSystem createSecureFileSystem = Robolectric.buildActivity(CreateSecureFileSystem.class, startCreateSFS)
                .create()
                .get();



    }

    @Test
    public void sanityTestCreateSFS() {

        String expectedFileName = "expectedFile";
        File expectedFile = new File(directory.getAbsolutePath() + File.separator + expectedFileName);

        FileSelectActivity activity = Robolectric.setupActivity(FileSelectActivity.class);

        FileWorkflow workflow = new FileWorkflow();
        workflow.setFileOrDirectory(directory);

        Intent startCreateSFS = new Intent(activity, CreateSecureFileSystem.class);
        startCreateSFS.putExtra("Workflow", workflow);

        CreateSecureFileSystem createSecureFileSystem = Robolectric.buildActivity(CreateSecureFileSystem.class, startCreateSFS)
                .create()
                .get();

        EditText fileName = createSecureFileSystem.findViewById(R.id.fileName);
        fileName.setText(expectedFileName);

        EditText password = createSecureFileSystem.findViewById(R.id.password);
        password.setText("password");

        password = createSecureFileSystem.findViewById(R.id.confirmPassword);
        password.setText("password");

        createSecureFileSystem.findViewById(R.id.ok).performClick();

        Awaitility.await().atMost(10, TimeUnit.SECONDS).until(() -> expectedFile.exists());
    }

}
