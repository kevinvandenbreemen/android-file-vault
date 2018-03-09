package com.vandenbreemen.mobilesecurestorage.android;

import android.content.Intent;
import android.os.Environment;
import android.widget.EditText;

import com.vandenbreemen.mobilesecurestorage.R;
import com.vandenbreemen.mobilesecurestorage.android.api.FileWorkflow;
import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials;
import com.vandenbreemen.mobilesecurestorage.file.ChunkedMediumException;
import com.vandenbreemen.mobilesecurestorage.security.SecureString;
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem;

import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowToast;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.plugins.RxJavaPlugins;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
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

    private FileWorkflow workflow;

    private Intent startCreateSFS;

    @Before
    public void setup() {
        this.directory = new File(Environment.getExternalStorageDirectory() + File.separator + "dir");
        directory.mkdir();

        //  This is a workaround to deal with issue in which
        //  the success callback never gets called
        //  https://github.com/robolectric/robolectric/issues/2534
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> AndroidSchedulers.mainThread());

        this.workflow = new FileWorkflow();
        this.workflow.setFileOrDirectory(directory);

        FileSelectActivity activity = Robolectric.setupActivity(FileSelectActivity.class);
        this.startCreateSFS = new Intent(activity, CreateSecureFileSystem.class);
        this.startCreateSFS.putExtra(FileWorkflow.PARM_WORKFLOW_NAME, workflow);
    }

    @Test
    public void sanityTestStartActivity() {

        CreateSecureFileSystem createSecureFileSystem = Robolectric.buildActivity(CreateSecureFileSystem.class, startCreateSFS)
                .create()
                .get();



    }

    @Test
    public void sanityTestCreateSFS() {

        String expectedFileName = "expectedFile";
        File expectedFile = new File(directory.getAbsolutePath() + File.separator + expectedFileName);

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

    @Test
    public void testMissingFilename() {

        CreateSecureFileSystem createSecureFileSystem = Robolectric.buildActivity(CreateSecureFileSystem.class, startCreateSFS)
                .create()
                .get();

        EditText password = createSecureFileSystem.findViewById(R.id.password);
        password.setText("password");

        password = createSecureFileSystem.findViewById(R.id.confirmPassword);
        password.setText("password");

        createSecureFileSystem.findViewById(R.id.ok).performClick();

        assertNotNull("Error toast", ShadowToast.getLatestToast());
    }

    @Test
    public void testCancel() {
        CreateSecureFileSystem createSecureFileSystem = Robolectric.buildActivity(CreateSecureFileSystem.class, startCreateSFS)
                .create()
                .get();

        createSecureFileSystem.findViewById(R.id.cancel).performClick();
    }

    @Test
    public void testnconsistentPassword() {

        CreateSecureFileSystem createSecureFileSystem = Robolectric.buildActivity(CreateSecureFileSystem.class, startCreateSFS)
                .create()
                .get();

        EditText fileName = createSecureFileSystem.findViewById(R.id.fileName);
        fileName.setText("filename");

        EditText password = createSecureFileSystem.findViewById(R.id.password);
        password.setText("password");

        password = createSecureFileSystem.findViewById(R.id.confirmPassword);
        password.setText("password1");

        createSecureFileSystem.findViewById(R.id.ok).performClick();

        assertNotNull("Error toast", ShadowToast.getLatestToast());
    }

    @Test
    public void testPasswordMissing() {
        CreateSecureFileSystem createSecureFileSystem = Robolectric.buildActivity(CreateSecureFileSystem.class, startCreateSFS)
                .create()
                .get();

        EditText fileName = createSecureFileSystem.findViewById(R.id.fileName);
        fileName.setText("filename");

        createSecureFileSystem.findViewById(R.id.ok).performClick();

        assertNotNull("Error toast", ShadowToast.getLatestToast());
    }

    /**
     * Validate credentials generated
     */
    @Test
    public void testCreateSFSGeneratesFileSystemCredentials() {

        AtomicBoolean tested = new AtomicBoolean(false);

        String expectedFileName = "expectedFile";
        File expectedFile = new File(directory.getAbsolutePath() + File.separator + expectedFileName);

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

    @Test
    public void testGoToFinalActivity() {

        String expectedFileName = "expectedFile";

        workflow.setActivityToStartAfterTargetActivityFinished(SecureFileSystemDetails.class);

        FileSelectActivity activity = Robolectric.setupActivity(FileSelectActivity.class);
        this.startCreateSFS = new Intent(activity, CreateSecureFileSystem.class);
        this.startCreateSFS.putExtra(FileWorkflow.PARM_WORKFLOW_NAME, workflow);

        CreateSecureFileSystem createSecureFileSystem = Robolectric.buildActivity(CreateSecureFileSystem.class, startCreateSFS)
                .create()
                .get();

        EditText fileName = createSecureFileSystem.findViewById(R.id.fileName);
        fileName.setText(expectedFileName);

        EditText password = createSecureFileSystem.findViewById(R.id.password);
        password.setText("password");

        password = createSecureFileSystem.findViewById(R.id.confirmPassword);
        password.setText("password");

        AtomicReference<Intent> nxtActivityRef = new AtomicReference<>(null);
        createSecureFileSystem.findViewById(R.id.ok).performClick();

        Awaitility.await().atMost(10, TimeUnit.SECONDS).until(() -> {
            nxtActivityRef.set(Shadows.shadowOf(createSecureFileSystem).getNextStartedActivity());
            return nxtActivityRef.get() != null;
        });

        Intent nextActivity = nxtActivityRef.get();
        ShadowIntent nxtActivityIntent = Shadows.shadowOf(nextActivity);

        assertEquals("Next activity", SecureFileSystemDetails.class, nxtActivityIntent.getIntentClass());
        assertNotNull("Credentials", nextActivity.getParcelableExtra(SFSCredentials.PARM_CREDENTIALS));
        assertNotNull("FS workflow", nextActivity.getParcelableExtra(FileWorkflow.PARM_WORKFLOW_NAME));
    }

}
