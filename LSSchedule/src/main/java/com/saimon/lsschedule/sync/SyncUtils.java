package com.saimon.lsschedule.sync;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import com.saimon.lsschedule.Constants;

/**
 * Created at 7:38 PM on 2/19/14
 * Copyright 2013 Poolsidelabs Inc.
 *
 * @author Saimon Rai
 */
public class SyncUtils {

    public static void forceSync(Context context) {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        ContentResolver.requestSync(SyncAccountManager.createSyncAccount(context),
                Constants.ContentProvider.AUTHORITY, settingsBundle);
    }

}
