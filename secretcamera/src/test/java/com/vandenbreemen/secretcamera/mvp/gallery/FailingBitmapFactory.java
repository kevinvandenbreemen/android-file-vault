package com.vandenbreemen.secretcamera.mvp.gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(BitmapFactory.class)
public class FailingBitmapFactory {

    @Implementation
    public static Bitmap decodeByteArray(byte[] data, int offset, int length) {
        throw new RuntimeException("Simulated byte array decode failure");
    }

}
