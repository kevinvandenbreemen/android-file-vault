package com.vandenbreemen.mobilesecurestorage.android.api;

import android.os.Environment;
import android.os.Parcel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;

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



}
