package com.saimon.lsschedule.sync;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.saimon.lsschedule.BuildConfig;
import com.saimon.lsschedule.Constants;
import com.saimon.lsschedule.model.Area;
import com.saimon.lsschedule.model.Group;
import com.saimon.lsschedule.model.GroupContainer;
import com.saimon.lsschedule.model.Schedule;
import com.saimon.lsschedule.model.Substation;
import com.saimon.lsschedule.provider.LSContract;
import com.saimon.lsschedule.volley.GsonRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created at 5:33 PM on 12/29/13
 * Copyright 2013 Poolsidelabs Inc.
 *
 * @author Saimon Rai
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = "SyncAdapter";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    ContentResolver mContentResolver;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }


    @Override
    public void onPerformSync(Account account,
                              Bundle bundle,
                              String s,
                              ContentProviderClient contentProviderClient,
                              SyncResult syncResult) {
        if (DEBUG) Log.e(TAG, "onPerformSync() called");

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        //TODO: These two operations should probably be done in a single transaction
        boolean groupsSynced = syncGroups(requestQueue);
        if (groupsSynced) {
            syncSchedule(requestQueue);
        }
    }

    private boolean syncGroups(RequestQueue requestQueue) {
        Uri uri = new Uri.Builder()
                .scheme(ApiConstants.SCHEME)
                .authority(ApiConstants.HOST)
                .path(ApiConstants.PATH_READ_GROUPS)
                .build();
        assert uri != null;
        RequestFuture<GroupContainer> future = RequestFuture.newFuture();

        GsonRequest<GroupContainer> request = new GsonRequest<GroupContainer>(
                Request.Method.GET,
                uri.toString(),
                GroupContainer.class,
                future, future);

        requestQueue.add(request);
        try {
            if (DEBUG) Log.i(TAG, "Fetching groups, substations, and areas... (" + uri + ")");
            GroupContainer groupContainer = future.get();
            List<Group> groups = groupContainer.getGroups();
            List<Substation> substations = groupContainer.getSubstations();
            List<Area> areas = groupContainer.getAreas();

            if (DEBUG) Log.i(TAG, "Fetched " + groups.size() + " groups, "
                    + substations.size() + " substations, "
                    + areas.size() + " areas.");

            ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
            // delete all the data stored in the content provider
            operations.add(ContentProviderOperation.newDelete(LSContract.Group.CONTENT_URI).build());
            operations.add(ContentProviderOperation.newDelete(LSContract.Substation.CONTENT_URI).build());
            operations.add(ContentProviderOperation.newDelete(LSContract.Area.CONTENT_URI).build());

            for (Group group : groups) {
                ContentValues cv = new ContentValues();
                cv.put(LSContract.Group._ID, group.getId());
                cv.put(LSContract.Group.NAME, group.getName());
                cv.put(LSContract.Group.NAME_NEP, group.getNameInNepali());
                operations.add(ContentProviderOperation.newInsert(LSContract.Group.CONTENT_URI).withValues(cv).build());
            }
            for (Substation substation : substations) {
                ContentValues cv = new ContentValues();
                cv.put(LSContract.Substation._ID, substation.getId());
                cv.put(LSContract.Substation.NAME, substation.getName());
                cv.put(LSContract.Substation.NAME_NEP, substation.getNameInNepali());
                cv.put(LSContract.Substation.IN_KTM, substation.isInKtm() ? 1 : 0);
                operations.add(ContentProviderOperation.newInsert(LSContract.Substation.CONTENT_URI).withValues(cv).build());
            }
            for (Area area : areas) {
                ContentValues cv = new ContentValues();
                cv.put(LSContract.Area._ID, area.getId());
                cv.put(LSContract.Area.NAME, area.getName());
                cv.put(LSContract.Area.NAME_NEP, area.getNameInNepali());
                cv.put(LSContract.Area.IN_KTM, area.isInKtm() ? 1 : 0);
                cv.put(LSContract.Area.GROUP_ID, area.getGroupId());
                cv.put(LSContract.Area.SUBSTATION_ID, area.getSubstationId());
                operations.add(ContentProviderOperation.newInsert(LSContract.Area.CONTENT_URI).withValues(cv).build());
            }
            try {
                if (DEBUG) Log.i(TAG, "Updating groups, substations, and areas...");
                mContentResolver.applyBatch(Constants.ContentProvider.AUTHORITY, operations);
                if (DEBUG) Log.i(TAG, "Sync complete.");
            } catch (RemoteException e) {
                if (DEBUG) Log.e(TAG, "RemoteException: " + e);
                return false;
            } catch (OperationApplicationException e) {
                if (DEBUG) Log.e(TAG, "OperationApplicationException: " + e);
                return false;
            }

        } catch (InterruptedException e) {
            if (DEBUG) Log.e(TAG, "InterruptedException: " + e);
            return false;
        } catch (ExecutionException e) {
            if (DEBUG) Log.e(TAG, "ExecutionException: " + e);
            return false;
        }

        return true;
    }

    private boolean syncSchedule(RequestQueue requestQueue) {
        Uri uri = new Uri.Builder()
                .scheme(ApiConstants.SCHEME)
                .authority(ApiConstants.HOST)
                .path(ApiConstants.PATH_READ_SCHEDULE)
                .build();
        assert uri != null;
        RequestFuture<Schedule[]> future = RequestFuture.newFuture();

        GsonRequest<Schedule[]> request = new GsonRequest<Schedule[]>(
                Request.Method.GET,
                uri.toString(),
                Schedule[].class,
                future, future);
        requestQueue.add(request);
        try {
            if (DEBUG) Log.i(TAG, "Fetching schedules... (" + uri + ")");
            List<Schedule> schedules = Arrays.asList(future.get());
            if (DEBUG) Log.i(TAG, "Fetched " + schedules.size() + " schedules.");

            //TODO: Convert this to bulkInsert using transactions.

            ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
            // delete all the schedule stored in the content provider
            operations.add(ContentProviderOperation.newDelete(LSContract.Schedule.CONTENT_URI).build());

            for (Schedule s : schedules) {
                ContentValues cv = new ContentValues();
                cv.put(LSContract.Schedule.GROUP_ID, s.getGroupId());
                cv.put(LSContract.Schedule.WEEKDAY, s.getWeekday());
                cv.put(LSContract.Schedule.START_TIME, s.getStartTime());
                cv.put(LSContract.Schedule.END_TIME, s.getEndTime());
                operations.add(ContentProviderOperation.newInsert(LSContract.Schedule.CONTENT_URI).withValues(cv).build());
            }

            try {
                if (DEBUG) Log.i(TAG, "Updating schedules...");
                mContentResolver.applyBatch(Constants.ContentProvider.AUTHORITY, operations);
                if (DEBUG) Log.i(TAG, "Sync complete.");
            } catch (RemoteException e) {
                if (DEBUG) Log.e(TAG, "RemoteException: " + e);
                return false;
            } catch (OperationApplicationException e) {
                if (DEBUG) Log.e(TAG, "OperationApplicationException: " + e);
                return false;
            }

        } catch (InterruptedException e) {
            if (DEBUG) Log.e(TAG, "InterruptedException: " + e);
            return false;
        } catch (ExecutionException e) {
            if (DEBUG) Log.e(TAG, "ExecutionException: " + e);
            return false;
        }

        return true;
    }

}
