package com.vandenbreemen.testutil;

import android.app.Activity;
import android.content.Intent;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.robolectric.shadows.ShadowIntent;

import static org.robolectric.Shadows.shadowOf;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class IntentMatchers {

    private static class ActivityMatcher extends TypeSafeMatcher<Intent> {

        private Class<? extends Activity> activity;

        /**
         *
         * @param activity
         */
        private ActivityMatcher(Class<? extends Activity> activity) {
            this.activity = activity;
        }

        @Override
        protected boolean matchesSafely(Intent item) {
            ShadowIntent intent = shadowOf(item);
            return intent.getIntentClass() != null && intent.getIntentClass().isAssignableFrom(activity);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Started intent not for activity ").appendText(activity.getSimpleName());
        }
    }

    /**
     * Check that the intent is for starting the given activity
     * @param clz
     * @return
     */
    public static ActivityMatcher matchesActivity(Class<? extends Activity> clz){
        return new ActivityMatcher(clz);
    }

}
