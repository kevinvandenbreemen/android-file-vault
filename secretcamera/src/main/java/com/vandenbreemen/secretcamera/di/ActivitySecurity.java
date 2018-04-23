package com.vandenbreemen.secretcamera.di;

import android.app.Activity;
import android.view.WindowManager;

public class ActivitySecurity {

    @FunctionalInterface
    public static interface ActivitySecurityPreparations {
        void configure(Activity activity);
    }

    private static ActivitySecurityPreparations preparations = (activity -> {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    });

    public static void setSecurity(Activity activity) {
        preparations.configure(activity);
    }

    static void setPreparations(ActivitySecurityPreparations preparations) {
        ActivitySecurity.preparations = preparations;
    }

}
