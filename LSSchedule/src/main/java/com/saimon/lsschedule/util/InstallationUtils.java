package com.saimon.lsschedule.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created at 8:09 PM on 2/13/14
 * Copyright 2013 Poolsidelabs Inc.
 *
 * @author Saimon Rai
 */
public class InstallationUtils {

    public static boolean isFirstLaunch(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("installation_info", 0);
        return prefs.getBoolean("is_first_launch", true);
    }

    public static void setFirstLaunchComplete(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("installation_info", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("is_first_launch", false);
        editor.commit();
    }


}
