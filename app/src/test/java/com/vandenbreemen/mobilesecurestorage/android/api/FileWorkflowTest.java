package com.vandenbreemen.mobilesecurestorage.android.api;

import android.os.Environment;
import android.os.Parcel;

import com.vandenbreemen.mobilesecurestorage.android.CreateSecureFileSystem;
import com.vandenbreemen.mobilesecurestorage.android.FileSelectActivity;
import com.vandenbreemen.mobilesecurestorage.android.LoadSecureFileSystem;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.AllOf.allOf;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
@RunWith(RobolectricTestRunner.class)
public class FileWorkflowTest {

    @Test
    public void testWriteFileLocation() {

        File expected = new File(Environment.getExternalStorageDirectory() + File.separator + "test");

        FileWorkflow workflow = new FileWorkflow();
        workflow.setFileOrDirectory(expected);

        Parcel parcel = Parcel.obtain();

        workflow.writeToParcel(parcel, workflow.describeContents());
        parcel.setDataPosition(0);

        FileWorkflow fromParcel = FileWorkflow.CREATOR.createFromParcel(parcel);
        File fileOrDir = fromParcel.getFileOrDirectory();

        assertThat(fileOrDir, allOf(
                notNullValue(),
                is(expected)
        ));

    }

    //  File workflow must provide activity to go to next with the data collected
    @Test
    public void testWriteTargetActivity() {
        FileWorkflow workflow = new FileWorkflow();
        workflow.setTargetActivity(CreateSecureFileSystem.class);

        Parcel parcel = Parcel.obtain();

        workflow.writeToParcel(parcel, workflow.describeContents());
        parcel.setDataPosition(0);

        FileWorkflow fromParcel = FileWorkflow.CREATOR.createFromParcel(parcel);

        assertEquals("Target activity", CreateSecureFileSystem.class, fromParcel.getTargetActivity());
    }

    @Test
    public void testOnFinishTargetActivity() {
        FileWorkflow workflow = new FileWorkflow();
        workflow.setTargetActivity(CreateSecureFileSystem.class);
        workflow.setActivityToStartAfterTargetActivityFinished(LoadSecureFileSystem.class);

        Parcel parcel = Parcel.obtain();

        workflow.writeToParcel(parcel, workflow.describeContents());
        parcel.setDataPosition(0);

        FileWorkflow fromParcel = FileWorkflow.CREATOR.createFromParcel(parcel);

        assertEquals("On Finish Target Activity", LoadSecureFileSystem.class, fromParcel.getActivityToStartAfterTargetActivityFinished());
    }

    @Test
    public void testCancelAction() {
        FileWorkflow workflow = new FileWorkflow();
        workflow.setTargetActivity(CreateSecureFileSystem.class);
        workflow.setActivityToStartAfterTargetActivityFinished(LoadSecureFileSystem.class);
        workflow.setCancelActivity(FileSelectActivity.class);

        Parcel parcel = Parcel.obtain();

        workflow.writeToParcel(parcel, workflow.describeContents());
        parcel.setDataPosition(0);

        FileWorkflow fromParcel = FileWorkflow.CREATOR.createFromParcel(parcel);

        assertEquals("Cancel Activity", FileSelectActivity.class, fromParcel.getCancelActivity());
    }

}
