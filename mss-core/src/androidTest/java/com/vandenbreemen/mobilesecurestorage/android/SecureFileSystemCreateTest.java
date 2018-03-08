package com.vandenbreemen.mobilesecurestorage.android;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.vandenbreemen.mobilesecurestorage.R;
import com.vandenbreemen.mobilesecurestorage.android.api.FileWorkflow;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * <h2>Intro
 * <p>Validate full workflow for creating a secure file system
 * <h2>Other Details
 *
 * @author kevin
 */
@RunWith(AndroidJUnit4.class)
public class SecureFileSystemCreateTest {

    @Rule
    public ActivityTestRule<FileSelectActivity> rule =
            new ActivityTestRule<FileSelectActivity>(FileSelectActivity.class);
    FileWorkflow workflow;

    @Before
    public void setup() {
        workflow = new FileWorkflow();
        workflow.setTargetActivity(CreateSecureFileSystem.class);

        Intent startActivity = new Intent();
        startActivity.putExtra(FileWorkflow.PARM_WORKFLOW_NAME, workflow);
        startActivity.putExtra(FileSelectActivity.PARM_DIR_ONLY, true);
        rule.launchActivity(startActivity);
    }

    //  Test straight up create new SFS
    @Test
    public void sanityTest() {
        onView(withText("Download")).perform(click());
        onView(withText("OK")).perform(click());

        onView(withId(R.id.fileName)).perform(typeText("testFile"));
        closeSoftKeyboard();
        onView(withId(R.id.password)).perform(typeText("password"));
        closeSoftKeyboard();
        onView(withId(R.id.confirmPassword)).perform(typeText("password"));
        closeSoftKeyboard();
        onView(withId(R.id.ok)).perform(click());
    }

}
