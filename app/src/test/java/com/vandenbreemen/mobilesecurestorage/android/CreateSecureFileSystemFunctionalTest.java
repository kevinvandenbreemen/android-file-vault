package com.vandenbreemen.mobilesecurestorage.android;

import android.content.Intent;
import android.os.Environment;
import android.widget.EditText;

import com.vandenbreemen.mobilesecurestorage.R;
import com.vandenbreemen.mobilesecurestorage.android.api.FileWorkflow;
import com.vandenbreemen.mobilesecurestorage.file.ChunkedMediumException;
import com.vandenbreemen.mobilesecurestorage.security.SecureString;
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem;

import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.plugins.RxJavaPlugins;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

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

        //  This is a workaround to deal with issue in which
        //  the success callback never gets called
        //  https://github.com/robolectric/robolectric/issues/2534
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> AndroidSchedulers.mainThread());
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

    /**
     * Validate credentials generated
     */
    @Test
    public void testCreateSFSGeneratesFileSystemCredentials() {

        AtomicBoolean tested = new AtomicBoolean(false);

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

        createSecureFileSystem.setOnCompleteListener(credentials -> {
            try {
                new SecureFileSystem(expectedFile) {
                    @Override
                    protected SecureString getPassword() {
                        return credentials.getPassword();
                    }
                };
                tested.set(true);
            } catch (ChunkedMediumException e) {
                e.printStackTrace();
                fail("Unexpected error");
            }
        });


        EditText fileName = createSecureFileSystem.findViewById(R.id.fileName);
        fileName.setText(expectedFileName);

        EditText password = createSecureFileSystem.findViewById(R.id.password);
        password.setText("password");

        password = createSecureFileSystem.findViewById(R.id.confirmPassword);
        password.setText("password");

        createSecureFileSystem.findViewById(R.id.ok).performClick();

        Awaitility.await().atMost(10, TimeUnit.SECONDS).until(() -> expectedFile.exists() && tested.get());

        assertTrue("SFS loadable using credentials", tested.get());
    }

}
