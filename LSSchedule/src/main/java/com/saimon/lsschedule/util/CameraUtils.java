package com.saimon.lsschedule.util;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created at 10:13 PM on 1/16/14
 * Copyright 2013 Poolsidelabs Inc.
 *
 * @author Saimon Rai
 */
public class CameraUtils {

    public static boolean isCameraSupported(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static boolean isCameraFlashSupported(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

}
