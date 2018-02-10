package com.vandenbreemen.mobilesecurestorage.android;

import android.Manifest;
import android.os.Environment;
import android.widget.ListView;

import com.vandenbreemen.mobilesecurestorage.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowListView;
import org.robolectric.shadows.ShadowLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertNotSame;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;

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

        Shadows.shadowOf(sut.getApplication()).grantPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        sut.onResume();


    }

    @Test
    public void testListsFiles() {
        ListView listView = sut.findViewById(R.id.fileList);

        ShadowListView shadow = Shadows.shadowOf(listView);
        shadow.populateItems();

        assertNotSame("File items displayed", 0, listView.getAdapter().getCount());
    }

    @Test
    public void testListsCorrectFilesOnStart() {
        ListView listView = sut.findViewById(R.id.fileList);

        ShadowListView shadow = Shadows.shadowOf(listView);
        shadow.populateItems();

        List<File> files = new ArrayList<>();
        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
            files.add((File) listView.getAdapter().getItem(i));
        }

        assertThat(files, allOf(
                hasItem(subDir),
                hasItem(fileInExtStorageDir),
                not(hasItem(fileInSubDir))
        ));
    }

}
