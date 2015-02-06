package com.saimon.lsschedule;

import android.app.Application;
import android.content.ContentResolver;
import android.os.Bundle;
import android.util.Log;

import com.saimon.lsschedule.provider.LSDatabase;
import com.saimon.lsschedule.sync.SyncAccountManager;
import com.saimon.lsschedule.sync.SyncUtils;
import com.saimon.lsschedule.util.DatabaseUtils;
import com.saimon.lsschedule.util.InstallationUtils;

/**
 * Created at 7:50 PM on 2/13/14
 * Copyright 2013 Poolsidelabs Inc.
 *
 * @author Saimon Rai
 */
public class App extends Application {
    private static final String TAG = "App";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    public App() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        boolean isFirstLaunch = InstallationUtils.isFirstLaunch(this);
        if (DEBUG) Log.d(TAG, "isFirstLaunch: " + isFirstLaunch);
        if (isFirstLaunch) {
            if (DEBUG) Log.d(TAG, "Syncing on first launch...");
            SyncUtils.forceSync(this);
        }
    }

}
