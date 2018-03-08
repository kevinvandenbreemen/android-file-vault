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
 * <p>
 * <h2>Other Details
 *
 * @author kevin
 */
@RunWith(AndroidJUnit4.class)
public class SecureFileSystemAccessTest {

    @Rule
    public ActivityTestRule<FileSelectActivity> rule = new
            ActivityTestRule<FileSelectActivity>(FileSelectActivity.class);
    private FileWorkflow worklfow;

    @Before
    public void setup() {
        worklfow = new FileWorkflow();
        worklfow.setTargetActivity(LoadSecureFileSystem.class);

        Intent start = new Intent();
        start.putExtra(FileWorkflow.PARM_WORKFLOW_NAME, worklfow);
        rule.launchActivity(start);

    }

    @Test
    public void sanityTest() {
        onView(withText("Download")).perform(click());
        onView(withText("testFile")).perform(click());
        onView(withId(R.id.ok)).perform(click());
        onView(withId(R.id.password)).perform(typeText("password"));
        closeSoftKeyboard();
        onView(withId(R.id.ok)).perform(click());
    }

    @Test
    public void testBadPassword() {
        onView(withText("Download")).perform(click());
        onView(withText("testFile")).perform(click());
        onView(withId(R.id.ok)).perform(click());
        onView(withId(R.id.password)).perform(typeText("password111"));
        closeSoftKeyboard();
        onView(withId(R.id.ok)).perform(click());
    }

}
