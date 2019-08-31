package com.vandenbreemen.mobilesecurestorage.android.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;

import com.vandenbreemen.mobilesecurestorage.R;
import com.vandenbreemen.mobilesecurestorage.android.CreateSecureFileSystem;
import com.vandenbreemen.mobilesecurestorage.android.LoadSecureFileSystem;
import com.vandenbreemen.mobilesecurestorage.android.api.FileWorkflow;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.vandenbreemen.testutil.IntentMatchers.matchesActivity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
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
public class SFSNavFragmentTest {

    @Test
    public void testClickNewSFS(){
        Bundle arguments = new Bundle();
        FileWorkflow workflow = new FileWorkflow();
        arguments.putParcelable(FileWorkflow.PARM_WORKFLOW_NAME, workflow);

        FragmentScenario<SFSNavFragment> scenario = FragmentScenario.launchInContainer(SFSNavFragment.class, arguments);
        scenario.onFragment(fragment -> fragment.getView().findViewById(R.id.createNew).performClick());

        scenario.onFragment(fragment -> {
            Intent nextActivity = shadowOf(fragment.getActivity()).getNextStartedActivityForResult().intent;
            assertThat("Kick off Start New SFS", nextActivity, allOf(
                    notNullValue(), matchesActivity(CreateSecureFileSystem.class)
            ));
        });

    }

    @Test
    public void testClickLoadSFS(){
        Bundle arguments = new Bundle();
        FileWorkflow workflow = new FileWorkflow();
        arguments.putParcelable(FileWorkflow.PARM_WORKFLOW_NAME, workflow);

        FragmentScenario<SFSNavFragment> scenario = FragmentScenario.launchInContainer(SFSNavFragment.class, arguments);

        scenario.onFragment(fragment -> fragment.getView().findViewById(R.id.loadExisting).performClick());

        scenario.onFragment(fragment -> {
            Intent nextActivity = shadowOf(fragment.getActivity()).getNextStartedActivityForResult().intent;
            assertThat("Kick off Loading Existing SFS", nextActivity, allOf(
                    notNullValue(), matchesActivity(LoadSecureFileSystem.class)
            ));
        });

    }

}
