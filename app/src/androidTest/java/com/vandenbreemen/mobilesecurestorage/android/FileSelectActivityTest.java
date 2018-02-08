package com.vandenbreemen.mobilesecurestorage.android;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * <h2>Intro
 * <p>
 * <h2>Other Details
 *
 * @author kevin
 */
@RunWith(AndroidJUnit4.class)
public class FileSelectActivityTest {

    @Rule
    public ActivityTestRule<FileSelectActivity> rule =
            new ActivityTestRule<FileSelectActivity>(FileSelectActivity.class);

    @Test
    public void sanityTestStart() {

    }

}
