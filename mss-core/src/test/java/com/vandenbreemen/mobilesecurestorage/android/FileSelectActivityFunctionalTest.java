package com.vandenbreemen.mobilesecurestorage.android;

import android.Manifest;
import android.content.Intent;
import android.os.Environment;
import android.widget.ListView;

import com.vandenbreemen.mobilesecurestorage.R;
import com.vandenbreemen.mobilesecurestorage.android.api.FileWorkflow;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowListView;
import org.robolectric.shadows.ShadowLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.vandenbreemen.mobilesecurestorage.android.FileSelectActivity.PARM_DIR_ONLY;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNotSame;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;
import static org.robolectric.Shadows.shadowOf;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
@RunWith(RobolectricTestRunner.class)
public class FileSelectActivityFunctionalTest {
    /**
     * App.  Using this to simulate having/not having permissions
     */
    private ShadowApplication app;

    private File subDir;

    private File fileInExtStorageDir;

    /**
     * File inside the {@link #subDir}
     */
    private File fileInSubDir;

    /**
     * System under test
     */
    private FileSelectActivity sut;

    @Before
    public void setup() throws Exception {

        //  Log to sys out
        ShadowLog.stream = System.out;

        fileInExtStorageDir = new File(Environment.getExternalStorageDirectory() + File.separator + "test");
        fileInExtStorageDir.createNewFile();
        subDir = new File(Environment.getExternalStorageDirectory() + File.separator + "subdir");
        subDir.mkdir();
        fileInSubDir = new File(Environment.getExternalStorageDirectory() + File.separator + "subdir"
                + File.separator + "subFile");
        fileInSubDir.createNewFile();


        //sut = Robolectric.setupActivity(FileSelectActivity.class);
        sut = Robolectric.buildActivity(FileSelectActivity.class)
                .create()
                .get();

        shadowOf(sut.getApplication()).grantPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        sut.onResume();


    }

    @Test
    public void testListsFiles() {
        ListView listView = sut.findViewById(R.id.fileList);

        ShadowListView shadow = shadowOf(listView);
        shadow.populateItems();

        assertNotSame("File items displayed", 0, listView.getAdapter().getCount());
    }

    @Test
    public void testGracefulCancelCancelUndefinedOnIntent() {
        ShadowLog.stream = System.out;
        sut.findViewById(R.id.cancel).performClick();
    }

    @Test
    public void testCancelActivity() {
        ShadowLog.stream = System.out;
        Intent startFileSelect = new Intent(sut.getApplication(), FileSelectActivity.class);
        startFileSelect.putExtra(PARM_DIR_ONLY, Boolean.TRUE);
        FileWorkflow workflow = new FileWorkflow();
        workflow.setCancelActivity(FileSelectActivity.class);
        startFileSelect.putExtra(FileWorkflow.PARM_WORKFLOW_NAME, workflow);

        sut = Robolectric.buildActivity(FileSelectActivity.class, startFileSelect)
                .create()
                .get();

        sut.onResume();

        sut.findViewById(R.id.cancel).performClick();
        ShadowIntent intent = shadowOf(shadowOf(sut).getNextStartedActivity());
        assertEquals("Next activity", FileSelectActivity.class, intent.getIntentClass());
    }

    @Test
    public void testListsCorrectFilesOnStart() {
        ListView listView = sut.findViewById(R.id.fileList);

        ShadowListView shadow = shadowOf(listView);
        shadow.populateItems();

        List<File> files = getDisplayedFiles(listView);

        assertThat(files, allOf(
                hasItem(subDir),
                hasItem(fileInExtStorageDir),
                not(hasItem(fileInSubDir))
        ));
    }

    //  Validate directory selector
    @Test
    public void testSelectDirectory() {
        ListView listView = sut.findViewById(R.id.fileList);

        ShadowListView shadow = shadowOf(listView);
        shadow.populateItems();

        int directoryIndex = 0;
        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
            if (((File) listView.getAdapter().getItem(i)).isDirectory()) {
                directoryIndex = i;
                break;
            }
        }

        shadow.performItemClick(directoryIndex);

        List<File> files = getDisplayedFiles(listView);

        assertThat(files, allOf(
                not(hasItem(subDir)),
                hasItem(fileInSubDir)
        ));

    }

    @Test
    public void testSelectFile() {
        ListView listView = sut.findViewById(R.id.fileList);

        ShadowListView shadow = shadowOf(listView);
        shadow.populateItems();

        int fileIndex = 0;
        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
            if (!((File) listView.getAdapter().getItem(i)).isDirectory()) {
                fileIndex = i;
                break;
            }
        }

        shadow.performItemClick(fileIndex);

        List<File> files = getDisplayedFiles(listView);

        assertThat(files, allOf(
                hasItem(subDir),
                hasItem(fileInExtStorageDir),
                not(hasItem(fileInSubDir))
        ));
    }

    @Test
    public void testConfirmFile() {

        AtomicReference<File> selectedFile = new AtomicReference<>(null);
        FileSelectActivity.FileSelectListener listener = selectedFile::set;
        sut.setListener(listener);

        ListView listView = sut.findViewById(R.id.fileList);

        ShadowListView shadow = shadowOf(listView);
        shadow.populateItems();

        int fileIndex = 0;
        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
            if (!((File) listView.getAdapter().getItem(i)).isDirectory()) {
                fileIndex = i;
                break;
            }
        }

        shadow.performItemClick(fileIndex);

        sut.findViewById(R.id.ok).performClick();

        assertEquals("Selected file", fileInExtStorageDir, selectedFile.get());
    }

    @Test
    public void testSelectAndConfirm() {

        Intent startListFile = new Intent(ShadowApplication.getInstance().getApplicationContext(), FileSelectActivity.class);
        startListFile.putExtra(FileSelectActivity.PARM_NO_CONFIRM_NEEDED, Boolean.TRUE);

        sut = Robolectric.buildActivity(FileSelectActivity.class, startListFile)
                .create()
                .get();

        shadowOf(sut.getApplication()).grantPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        sut.onResume();

        AtomicReference<File> selectedFile = new AtomicReference<>(null);
        FileSelectActivity.FileSelectListener listener = selectedFile::set;
        sut.setListener(listener);

        ListView listView = sut.findViewById(R.id.fileList);

        ShadowListView shadow = shadowOf(listView);
        shadow.populateItems();

        int fileIndex = 0;
        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
            if (!((File) listView.getAdapter().getItem(i)).isDirectory()) {
                fileIndex = i;
                break;
            }
        }

        shadow.performItemClick(fileIndex);

        assertEquals("Selected file", fileInExtStorageDir, selectedFile.get());
    }

    //  System should gracefully act when no listener/intent/etc set up for confirmed file
    @Test
    public void sanityConfirmFile() {

        ListView listView = sut.findViewById(R.id.fileList);

        ShadowListView shadow = shadowOf(listView);
        shadow.populateItems();

        int fileIndex = 0;
        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
            if (!((File) listView.getAdapter().getItem(i)).isDirectory()) {
                fileIndex = i;
                break;
            }
        }

        shadow.performItemClick(fileIndex);

        sut.findViewById(R.id.ok).performClick();
    }

    @Test
    public void testSelectDirectoryOnly() {

        Intent startFileSelect = new Intent(sut.getApplication(), FileSelectActivity.class);
        startFileSelect.putExtra(PARM_DIR_ONLY, Boolean.TRUE);

        sut = Robolectric.buildActivity(FileSelectActivity.class, startFileSelect)
                .create()
                .get();

        sut.onResume();

        ListView listView = sut.findViewById(R.id.fileList);

        ShadowListView shadow = shadowOf(listView);
        shadow.populateItems();

        List<File> files = getDisplayedFiles(listView);

        assertThat(files, allOf(
                hasItem(subDir),
                not(hasItem(fileInExtStorageDir))
        ));
    }

    //  See https://stackoverflow.com/a/39674693
    @Test
    public void testStartNextActivity() {

        FileWorkflow workflow = new FileWorkflow();
        workflow.setTargetActivity(CreateSecureFileSystem.class);

        Intent startFileSelect = new Intent(sut.getApplication(), FileSelectActivity.class);
        startFileSelect.putExtra(PARM_DIR_ONLY, Boolean.TRUE);
        startFileSelect.putExtra(FileWorkflow.PARM_WORKFLOW_NAME, workflow);

        sut = Robolectric.buildActivity(FileSelectActivity.class, startFileSelect)
                .create()
                .get();

        sut.onResume();

        ListView listView = sut.findViewById(R.id.fileList);

        ShadowListView shadow = shadowOf(listView);
        shadow.populateItems();

        int fileIndex = 0;
        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
            if (((File) listView.getAdapter().getItem(i)).isDirectory()) {
                fileIndex = i;
                break;
            }
        }

        shadow.performItemClick(fileIndex);

        sut.findViewById(R.id.ok).performClick();

        //  Validate activity started
        //  https://stackoverflow.com/a/39674693
        Intent nextActivity = shadowOf(sut).getNextStartedActivity();
        ShadowIntent nxtActivityIntent = shadowOf(nextActivity);

        assertEquals("Next Activity", CreateSecureFileSystem.class, nxtActivityIntent.getIntentClass());

        FileWorkflow workflowParcel = nextActivity.getParcelableExtra(FileWorkflow.PARM_WORKFLOW_NAME);
        assertNotNull("workflow", workflowParcel);
        assertEquals("Selected directory", subDir, workflowParcel.getFileOrDirectory());
    }

    @Test
    public void testConfirmDirectorySelection() {

        Intent startFileSelect = new Intent(sut.getApplication(), FileSelectActivity.class);
        startFileSelect.putExtra(PARM_DIR_ONLY, Boolean.TRUE);

        sut = Robolectric.buildActivity(FileSelectActivity.class, startFileSelect)
                .create()
                .get();

        sut.onResume();

        AtomicReference<File> selectedFile = new AtomicReference<>(null);
        FileSelectActivity.FileSelectListener listener = selectedFile::set;
        sut.setListener(listener);

        ListView listView = sut.findViewById(R.id.fileList);

        ShadowListView shadow = shadowOf(listView);
        shadow.populateItems();

        int fileIndex = 0;
        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
            if (((File) listView.getAdapter().getItem(i)).isDirectory()) {
                fileIndex = i;
                break;
            }
        }

        shadow.performItemClick(fileIndex);

        sut.findViewById(R.id.ok).performClick();

        assertEquals("Selected file", subDir, selectedFile.get());
    }

    private List<File> getDisplayedFiles(ListView listView) {
        List<File> files = new ArrayList<>();
        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
            files.add((File) listView.getAdapter().getItem(i));
        }
        return files;
    }

}
