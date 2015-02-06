package com.saimon.lsschedule.provider;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.saimon.lsschedule.BuildConfig;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created at 6:22 PM on 12/29/13
 * Copyright 2013 Poolsidelabs Inc.
 *
 * @author Saimon Rai
 */
public class LSProvider extends ContentProvider {
    public static final String TAG = "LSProvider";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final UriMatcher URI_MATCHER = buildUriMatcher();

    private static final int SEARCH_SUGGEST = 10;

    private static final int GROUPS = 20;
    private static final int GROUP_ID = 21;

    private static final int AREAS_WITH_SUBSTATIONS = 30;
    private static final int SUBSTATIONS = 31;
    private static final int SUBSTATION_ID = 32;

    private static final int AREAS = 40;
    private static final int AREA_ID = 41;

    private static final int SCHEDULES = 50;

    private LSDatabase mOpenHelper;
    private SQLiteDatabase db;

    /**
     * Build and return a {@link UriMatcher} that catches all {@link Uri}
     * variations supported by this {@link ContentProvider}.
     *
     * @return the uri matcher
     */
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = LSContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, LSContract.PATH_GROUPS, GROUPS);
        matcher.addURI(authority, LSContract.PATH_GROUPS + "/*", GROUP_ID);

        // "areas/with_substations/*"
        matcher.addURI(authority, LSContract.PATH_AREAS + "/" + LSContract.PATH_WITH_SUBSTATION, AREAS_WITH_SUBSTATIONS);
        matcher.addURI(authority, LSContract.PATH_SUBSTATIONS, SUBSTATIONS);
        matcher.addURI(authority, LSContract.PATH_SUBSTATIONS + "/*", SUBSTATION_ID);

        matcher.addURI(authority, LSContract.PATH_AREAS, AREAS);
        matcher.addURI(authority, LSContract.PATH_AREAS + "/*", AREA_ID);

        matcher.addURI(authority, LSContract.PATH_SCHEDULES, SCHEDULES);

        matcher.addURI(authority, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        matcher.addURI(authority, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        final Context context = getContext();
        mOpenHelper = new LSDatabase(context);
        db = mOpenHelper.getReadableDatabase();
        return true;
    }

    @Override
    public String getType(Uri uri) {
        if (DEBUG) Log.d(TAG, "getType():  " + uri.toString());

        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case GROUPS:
                return LSContract.Group.CONTENT_TYPE;
            case GROUP_ID:
                return LSContract.Group.CONTENT_ITEM_TYPE;
            case SUBSTATIONS:
                return LSContract.Substation.CONTENT_TYPE;
            case SUBSTATION_ID:
                return LSContract.Substation.CONTENT_ITEM_TYPE;
            case AREAS:
                return LSContract.Area.CONTENT_TYPE;
            case AREA_ID:
                return LSContract.Area.CONTENT_ITEM_TYPE;
            case AREAS_WITH_SUBSTATIONS:
                return LSContract.Area.CONTENT_TYPE;
            case SCHEDULES:
                return LSContract.Schedule.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        if (DEBUG) Log.v(TAG, "query(uri=" + uri + ", proj=" + Arrays.toString(projection) + ")");

        SelectionBuilder builder = new SelectionBuilder();

        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = URI_MATCHER.match(uri);

        Cursor cursor;
        Context context = getContext();
        assert context != null;

        switch (match) {
            case GROUP_ID: {
                builder.where(LSContract.Group._ID + "=?" + uri.getLastPathSegment());
                // Note that there is no 'break' statement here.
            }
            case GROUPS: {
                builder.table(LSDatabase.Tables.GROUP).where(selection, selectionArgs);
                cursor = builder.query(db, projection, sortOrder);
                break;
            }
            case SUBSTATION_ID: {
                builder.where(LSContract.Substation._ID + "=?" + uri.getLastPathSegment());
                // Note that there is no 'break' statement here.
            }
            case SUBSTATIONS: {
                builder.table(LSDatabase.Tables.SUBSTATION).where(selection, selectionArgs);
                cursor = builder.query(db, projection, sortOrder);
                break;
            }
            case AREA_ID: {
                builder.where(LSContract.Group._ID + "=?" + uri.getLastPathSegment());
                // Note that there is no 'break' statement here.
            }
            case AREAS: {
                builder.table(LSDatabase.Tables.AREA).where(selection, selectionArgs);
                cursor = builder.query(db, projection, sortOrder);
                break;
            }
            case SCHEDULES: {
                builder.table(LSDatabase.Tables.SCHEDULE).where(selection, selectionArgs);
                cursor = builder.query(db, projection, sortOrder);
                break;
            }
            case AREAS_WITH_SUBSTATIONS: {
                builder.table(LSDatabase.Tables.AREAS_JOIN_SUBSTATIONS)
                        .mapToTable(LSContract.Area._ID, LSDatabase.Tables.AREA)
                        .mapToTable(LSContract.Area.NAME, LSDatabase.Tables.AREA)
                        .mapToTable(LSContract.Area.NAME_NEP, LSDatabase.Tables.AREA)
                        .mapToTable(LSContract.Area.IN_KTM, LSDatabase.Tables.AREA)

                        // equivalent > .map("substation_name", "substation.name")
                        .map(LSDatabase.Tables.SUBSTATION + "_" + LSContract.Substation.NAME, LSDatabase.Tables.SUBSTATION + "." + LSContract.Substation.NAME)
                        // equivalent > .map("substation_name_nep", "substation.name_nep")
                        .map(LSDatabase.Tables.SUBSTATION + "_" + LSContract.Substation.NAME_NEP, LSDatabase.Tables.SUBSTATION + "." + LSContract.Substation.NAME_NEP)
                        // equivalent > .map("substation_in_ktm", "substation.in_ktm");
                        .map(LSDatabase.Tables.SUBSTATION + "_" + LSContract.Substation.IN_KTM, LSDatabase.Tables.SUBSTATION + "." + LSContract.Substation.IN_KTM);

                cursor = builder.where(selection, selectionArgs).query(db, projection, sortOrder);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        cursor.setNotificationUri(context.getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (DEBUG) Log.v(TAG, "insert(uri=" + uri + ", values=" + values.toString() + ")");

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);

        long _id;
        Uri result;

        switch (match) {
            case GROUPS: {
                _id = db.insertOrThrow(LSDatabase.Tables.GROUP, null, values);
                result = ContentUris.withAppendedId(LSContract.Group.CONTENT_URI, _id);
                break;
            }
            case SUBSTATIONS: {
                _id = db.insertOrThrow(LSDatabase.Tables.SUBSTATION, null, values);
                result = ContentUris.withAppendedId(LSContract.Substation.CONTENT_URI, _id);
                break;
            }
            case AREAS: {
                _id = db.insertOrThrow(LSDatabase.Tables.AREA, null, values);
                result = ContentUris.withAppendedId(LSContract.Area.CONTENT_URI, _id);
                break;
            }
            case SCHEDULES: {
                _id = db.insertOrThrow(LSDatabase.Tables.SCHEDULE, null, values);
                result = ContentUris.withAppendedId(LSContract.Schedule.CONTENT_URI, _id);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null);

        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);

        int count;

        switch (match) {
            case GROUPS: {
                count = builder.table(LSDatabase.Tables.GROUP)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            }
            case GROUP_ID: {
                count = builder.table(LSDatabase.Tables.GROUP)
                        .where(LSContract.Group._ID + "=?", uri.getLastPathSegment())
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            }
            case SUBSTATIONS: {
                count = builder.table(LSDatabase.Tables.SUBSTATION)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            }
            case SUBSTATION_ID: {
                count = builder.table(LSDatabase.Tables.SUBSTATION)
                        .where(LSContract.Substation._ID + "=?", uri.getLastPathSegment())
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            }
            case AREAS: {
                count = builder.table(LSDatabase.Tables.AREA)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            }
            case AREA_ID: {
                count = builder.table(LSDatabase.Tables.AREA)
                        .where(LSContract.Area._ID + "=?", uri.getLastPathSegment())
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            }
            case SCHEDULES: {
                count = builder.table(LSDatabase.Tables.SCHEDULE)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);

        int count;

        switch (match) {
            case GROUPS: {
                count = builder.table(LSDatabase.Tables.GROUP)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            }
            case GROUP_ID: {
                count = builder.table(LSDatabase.Tables.GROUP)
                        .where(LSContract.Group._ID + "=?", uri.getLastPathSegment())
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            }
            case SUBSTATIONS: {
                count = builder.table(LSDatabase.Tables.SUBSTATION)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            }
            case SUBSTATION_ID: {
                count = builder.table(LSDatabase.Tables.SUBSTATION)
                        .where(LSContract.Substation._ID + "=?", uri.getLastPathSegment())
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            }
            case AREAS: {
                count = builder.table(LSDatabase.Tables.AREA)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            }
            case AREA_ID: {
                count = builder.table(LSDatabase.Tables.AREA)
                        .where(LSContract.Area._ID + "=?", uri.getLastPathSegment())
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            }
            case SCHEDULES: {
                count = builder.table(LSDatabase.Tables.SCHEDULE)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null);
        return count;
    }
}
