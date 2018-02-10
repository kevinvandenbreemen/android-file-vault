package com.vandenbreemen.mobilesecurestorage.android;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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


    @Test
    public void sanityTestStart() {

    }

    @Test
    public void testAllowFileAccess() throws Exception{

        //  Having trouble getting thjis to work due to device state issues
        //  https://blog.egorand.me/testing-runtime-permissions-lessons-learned/

        //  See https://github.com/googlesamples/android-testing-templates/blob/master/AndroidTestingBlueprint/app/src/androidTest/java/com/example/android/testing/blueprint/ui/uiautomator/UiAutomatorTest.java
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        UiObject allowPermissions = device.findObject(new UiSelector().text("Allow"));
        assertTrue("File permissions request",allowPermissions.exists());

    }

}
