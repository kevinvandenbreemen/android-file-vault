package com.vandenbreemen.mobilesecurestorage.android;

import android.content.Intent;
import android.os.Environment;
import android.widget.TextView;

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

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.plugins.RxJavaPlugins;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
@RunWith(RobolectricTestRunner.class)
public class LoadSecureFileSystemFunctionalTest {

    private String password;

    private FileWorkflow workflow;

    private Intent startLoadSFS;

    @Before
    public void setup() throws ChunkedMediumException {

        //  This is a workaround to deal with issue in which
        //  the success callback never gets called
        //  https://github.com/robolectric/robolectric/issues/2534
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> AndroidSchedulers.mainThread());

        File sfsFile = new File(Environment.getExternalStorageDirectory() + File.separator + "test");
        password = "password";

        //  Stand up SFS
        new SecureFileSystem(sfsFile) {
            @Override
            protected SecureString getPassword() {
                return SecureFileSystem.generatePassword(SecureString.fromPassword(password));
            }
        };

        this.workflow = new FileWorkflow();
        this.workflow.setFileOrDirectory(sfsFile);

        FileSelectActivity activity = Robolectric.setupActivity(FileSelectActivity.class);
        this.startLoadSFS = new Intent(activity, LoadSecureFileSystem.class);
        this.startLoadSFS.putExtra(FileWorkflow.PARM_WORKFLOW_NAME, workflow);
    }

    @Test
    public void sanityTestLoadFile() {
        LoadSecureFileSystem load = Robolectric.buildActivity(LoadSecureFileSystem.class, startLoadSFS)
                .create()
                .get();

        TextView textView = load.findViewById(R.id.password);
        textView.setText(password);

        load.findViewById(R.id.ok).performClick();
    }

    @Test
    public void testGetCredentails() {

        AtomicReference<SFSCredentials> credentials = new AtomicReference<>(null);

        LoadSecureFileSystem load = Robolectric.buildActivity(LoadSecureFileSystem.class, startLoadSFS)
                .create()
                .get();

        load.setListener(cred -> credentials.set(cred));

        TextView textView = load.findViewById(R.id.password);
        textView.setText(password);

        load.findViewById(R.id.ok).performClick();

        Awaitility.await().atMost(10, TimeUnit.SECONDS).until(() -> credentials.get() != null);

        assertTrue("Password",
                SecureFileSystem.generatePassword(SecureString.fromPassword(password)).equals(credentials.get().getPassword()));
    }

    @Test
    public void testGoToFinalActivity() {
        FileSelectActivity activity = Robolectric.setupActivity(FileSelectActivity.class);
        workflow.setActivityToStartAfterTargetActivityFinished(SecureFileSystemDetails.class);
        startLoadSFS = new Intent(activity, LoadSecureFileSystem.class);
        this.startLoadSFS.putExtra(FileWorkflow.PARM_WORKFLOW_NAME, workflow);

        LoadSecureFileSystem load = Robolectric.buildActivity(LoadSecureFileSystem.class, startLoadSFS)
                .create()
                .get();

        TextView textView = load.findViewById(R.id.password);
        textView.setText(password);

        AtomicReference<Intent> nxtActivityRef = new AtomicReference<>(null);
        load.findViewById(R.id.ok).performClick();
        Awaitility.await().atMost(10, TimeUnit.SECONDS).until(() -> {
            nxtActivityRef.set(Shadows.shadowOf(load).getNextStartedActivity());
            return nxtActivityRef.get() != null;
        });

        //  Validate activity started
        //  https://stackoverflow.com/a/39674693
        Intent nextActivity = nxtActivityRef.get();
        ShadowIntent nxtActivityIntent = Shadows.shadowOf(nextActivity);
        assertEquals("Next activity", SecureFileSystemDetails.class, nxtActivityIntent.getIntentClass());
        assertNotNull("Credentials", nextActivity.getParcelableExtra(SFSCredentials.PARM_CREDENTIALS));
    }

}
