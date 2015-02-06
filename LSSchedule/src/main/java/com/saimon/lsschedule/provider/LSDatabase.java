package com.saimon.lsschedule.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.saimon.lsschedule.BuildConfig;
import com.saimon.lsschedule.util.DatabaseUtils;

/**
 * Created at 7:17 PM on 12/29/13
 * Copyright 2013 Poolsidelabs Inc.
 *
 * @author Saimon Rai
 */
public class LSDatabase extends SQLiteAssetHelper { //SQLiteOpenHelper {
    public static final String TAG = "LSDatabase";
    private static boolean DEBUG = BuildConfig.DEBUG;

    public static final String DATABASE_NAME = "ls_schedule.db";
    private static final int DATABASE_VERSION = 2;

    interface Tables {
        String GROUP = "ls_group";
        String SUBSTATION = "substation";
        String AREA = "area";
        String SCHEDULE = "schedule";

        String AREAS_JOIN_SUBSTATIONS = "area " +
                "INNER JOIN substation ON area.substation_id=substation._id";
    }

    public LSDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }


//    public LSDatabase(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE " + Tables.GROUP + " ("
//                + LSContract.Group._ID + " INTEGER PRIMARY KEY,"
//                + LSContract.Group.NAME + " TEXT, "
//                + LSContract.Group.NAME_NEP + " TEXT"
//                + ")");
//
//        db.execSQL("CREATE TABLE " + Tables.SUBSTATION + " ("
//                + LSContract.Substation._ID + " INTEGER PRIMARY KEY,"
//                + LSContract.Substation.NAME + " TEXT, "
//                + LSContract.Substation.NAME_NEP + " TEXT, "
//                + LSContract.Substation.IN_KTM + " INTEGER"
//                + ")");
//
//        db.execSQL("CREATE TABLE " + Tables.AREA + " ("
//                + LSContract.Area._ID + " INTEGER PRIMARY KEY, "
//                + LSContract.Area.NAME + " TEXT, "
//                + LSContract.Area.NAME_NEP + " TEXT, "
//                + LSContract.Area.IN_KTM + " INTEGER, "
//                + LSContract.Area.GROUP_ID + " INTEGER, "
//                + LSContract.Area.SUBSTATION_ID + " INTEGER, "
//                + " FOREIGN KEY(" + LSContract.Area.GROUP_ID + ") REFERENCES " + Tables.GROUP + "(" + LSContract.Group._ID + "), "
//                + " FOREIGN KEY(" + LSContract.Area.SUBSTATION_ID + ") REFERENCES " + Tables.SUBSTATION + "(" + LSContract.Substation._ID + ")"
//                + ")");
//
//        db.execSQL("CREATE TABLE " + Tables.SCHEDULE + " ("
//                + LSContract.Schedule._ID + " INTEGER PRIMARY KEY, "
//                + LSContract.Schedule.GROUP_ID + " TEXT, "
//                + LSContract.Schedule.WEEKDAY + " TEXT, "
//                + LSContract.Schedule.START_TIME + " TEXT, "
//                + LSContract.Schedule.END_TIME + " TEXT"
//                + ")");
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        // Logs that the database is being upgraded
//        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
//                + newVersion + ", which will destroy all old data");
//
//        // Kills the table and existing data
//        db.execSQL("DROP TABLE IF EXISTS " + Tables.GROUP);
//        db.execSQL("DROP TABLE IF EXISTS " + Tables.SUBSTATION);
//        db.execSQL("DROP TABLE IF EXISTS " + Tables.AREA);
//        db.execSQL("DROP TABLE IF EXISTS " + Tables.SCHEDULE);
//
//        // Recreates the database with a new version
//        onCreate(db);
//    }

}
