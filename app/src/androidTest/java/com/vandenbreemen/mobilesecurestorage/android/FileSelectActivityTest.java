package com.vandenbreemen.mobilesecurestorage.android;

import android.Manifest;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;

import com.vandenbreemen.mobilesecurestorage.R;
import com.vandenbreemen.mobilesecurestorage.android.api.FileWorkflow;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertTrue;

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
    FileWorkflow workflow;

    @Before
    public void setup() {
        workflow = new FileWorkflow();
        workflow.setTargetActivity(CreateSecureFileSystem.class);

        Intent startActivity = new Intent();
        startActivity.putExtra(FileWorkflow.PARM_WORKFLOW_NAME, workflow);
        rule.launchActivity(startActivity);
    }

    //  Sadly I've not been able to reset permissions between tests
    //  One promising lead on this is https://blog.egorand.me/testing-runtime-permissions-lessons-learned/,
    //  but the logic for resetting the permissions is a hack (go into settings drawer, revoke permissions)
    //  and the use of adb through the instrumentation causes the test to crash.  I'm stymied as to how to
    //  effectively do a test like this...
    //  Using pm revoke (permission) below has yielded some positive results but the tests often crash with message
    //  Test instrumentation process crashed.  Check com.vandenbreemen.mobilesecurestorage.android.FileSelectActivityTest#testAllowPermission.txt for details
    //  However when the tests are successful (for example the two disabled tests below) I am able to repeat them (for example, I get the
    //  permission dialog each time I run the testAllowPermission() test.
    @Before
    public void grantPermissions() {
        getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + getTargetContext().getPackageName()
                        + " " + Manifest.permission.WRITE_EXTERNAL_STORAGE);
        getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + getTargetContext().getPackageName()
                        + " " + Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    @Test
    public void testDisplayFilesList() {
        onView(withId(R.id.fileList)).check(matches(isDisplayed()));
    }

    @Test
    public void testSelectDirectory() throws Exception {
        onView(withText("Download")).perform(click());
        onView(withText("OK")).perform(click());
    }

    //  Not stable
    //  @Test
    public void testPromptsForPermissionWhenNoneGiven() throws Exception {

        //  Having trouble getting thjis to work due to device state issues
        //  https://blog.egorand.me/testing-runtime-permissions-lessons-learned/

        //  See https://github.com/googlesamples/android-testing-templates/blob/master/AndroidTestingBlueprint/app/src/androidTest/java/com/example/android/testing/blueprint/ui/uiautomator/UiAutomatorTest.java
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        UiObject allowPermissions = device.findObject(new UiSelector().text("ALLOW"));
        assertTrue("File permissions request",allowPermissions.exists());


    }

    //  Not stable
    //@Test
    public void testAllowPermission() throws Exception {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        UiObject allowPermissions = device.findObject(new UiSelector().text("ALLOW"));
        allowPermissions.click();
    }



}
