package com.saimon.lsschedule.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.saimon.lsschedule.BuildConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

/**
 * Created at 6:10 PM on 2/13/14
 * Copyright 2013 Poolsidelabs Inc.
 *
 * @author Saimon Rai
 */
public class DatabaseUtils {
    private static final String TAG = "DatabaseUtils";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    public static boolean exportDatabase(String packageName, String databaseName) {
        File dataDirectory = new File(Environment.getDataDirectory(), "data");
        File packageDirectory = new File(dataDirectory, packageName);
        File databaseDirectory = new File(packageDirectory, "databases");
        File srcFile = new File(databaseDirectory, databaseName);

        // path should be like: /data/data/com.saimon.lsschedule/databases/ls_schedule.db
        if (DEBUG) {
            Log.d(TAG, "database file path: " + srcFile.getAbsolutePath() + ", exists: " + srcFile.exists());
        }
        if (!srcFile.exists()) {
            return false;
        }

        File dstDirectory = new File(Environment.getExternalStorageDirectory(), packageName);
        if (!dstDirectory.exists()) {
            dstDirectory.mkdir();
        }
        File dstFile = new File(dstDirectory, databaseName);
        if (DEBUG) {
            Log.d(TAG, "Copying '" + srcFile.getAbsolutePath() + "' to '" + dstFile.getAbsolutePath() + "'...");
        }

        try {
            FileChannel src = new FileInputStream(srcFile).getChannel();
            FileChannel dst = new FileOutputStream(dstFile).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
            Log.d(TAG, "Copying complete.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean importDatabase(Context context, String databaseName) {
        File dataDirectory = new File(Environment.getDataDirectory(), "data");
        File packageDirectory = new File(dataDirectory, context.getPackageName());
        File databaseDirectory = new File(packageDirectory, "databases");
        File destFile = new File(databaseDirectory, databaseName);

        if (!databaseDirectory.exists()) {
            databaseDirectory.mkdirs();
        }

        try {
            if (DEBUG) {
                Log.d(TAG, "Copying '" + databaseName + "' to '" + destFile.getAbsolutePath() + "'...");
            }

            InputStream srcStream = context.getAssets().open(databaseName);
            OutputStream destStream = new FileOutputStream(destFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = srcStream.read(buffer)) > 0){
                destStream.write(buffer, 0, length);
            }
            //Close the streams
            destStream.flush();
            destStream.close();
            srcStream.close();

            Log.d(TAG, "Copying complete.");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error importing database.");
            return false;
        }

        return true;
    }

    public static boolean doesDatabaseExist(Context context, String databaseName) {
        File file = context.getDatabasePath(databaseName);
        return file.exists();
    }

}
