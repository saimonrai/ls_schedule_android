package com.saimon.lsschedule.ui.fragment;

import android.content.ContentResolver;
import android.content.SyncStatusObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.saimon.lsschedule.BuildConfig;
import com.saimon.lsschedule.Constants;
import com.saimon.lsschedule.R;
import com.saimon.lsschedule.model.Group;
import com.saimon.lsschedule.provider.LSContract;
import com.saimon.lsschedule.sync.SyncAccountManager;

import java.util.ArrayList;
import java.util.List;


public class ScheduleGroupsFragment extends ProgressFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = "ScheduleGroupsFragment";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private ViewPager mPager;

    private Object mSyncStatusObserver;


    public static ScheduleGroupsFragment newInstance() {
        ScheduleGroupsFragment fragment = new ScheduleGroupsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public ScheduleGroupsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule_groups, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPager = (ViewPager)getContentView().findViewById(R.id.pager);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSyncStatusObserver = ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE,
                new SyncStatusObserver() {
                    @Override
                    public void onStatusChanged(int which) {
                        boolean isSyncActive = ContentResolver.isSyncActive(SyncAccountManager.createSyncAccount(getActivity()),
                                Constants.ContentProvider.AUTHORITY);
                        if (DEBUG) Log.d(TAG, "isSyncActive: " + isSyncActive);

                        if (!isSyncActive) {
                            // sync is complete
                            // if we still don't have any data show the error view.
                            if (mPager.getAdapter() == null || mPager.getAdapter().getCount() == 0) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showError(true);
                                    }
                                });
                            }
                        }
                    }
                });

        // http://stackoverflow.com/questions/11293441/android-loadercallbacks-onloadfinished-called-twice
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSyncStatusObserver != null) {
            ContentResolver.removeStatusChangeListener(mSyncStatusObserver);
        }
    }

    //==========================================================
    // Loader Callbacks
    //==========================================================

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                LSContract.Group.CONTENT_URI, null, null, null, LSContract.Group.NAME + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (DEBUG) Log.d(TAG, "Fetched " + cursor.getCount() + " groups.");

        if (cursor == null || cursor.getCount() == 0) {
            showError(true);
            return;
        }

        List<Group> groups = new ArrayList<Group>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            groups.add(Group.fromContentValues(cursor));
            cursor.moveToNext();
        }

        PageAdapter mPageAdapter = new PageAdapter(getChildFragmentManager(), groups);
        mPager.setAdapter(mPageAdapter);

        PagerSlidingTabStrip strip = (PagerSlidingTabStrip)getContentView().findViewById(R.id.tabs);
        strip.setIndicatorColorResource(R.color.tab_indicator);
        strip.setViewPager(mPager);

        showContent(true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {}

    //==========================================================
    // ProgressFragment error callback
    //==========================================================

    @Override
    protected void onRetryButtonClicked(View view) {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        ContentResolver.requestSync(SyncAccountManager.createSyncAccount(getActivity()),
                Constants.ContentProvider.AUTHORITY, settingsBundle);

        showProgress();
    }

    //==========================================================
    // ViewPager adapter
    //==========================================================

    private static class PageAdapter extends FragmentStatePagerAdapter {
        private List<Group> mGroups;

        public PageAdapter(FragmentManager fm, List<Group> groups) {
            super(fm);
            mGroups = groups;
        }

        @Override
        public int getCount() {
            return mGroups.size();
        }

        @Override
        public Fragment getItem(int position) {
            Group group = mGroups.get(position);
            return ScheduleFragment.newInstance(group.getId());
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mGroups.get(position).getName();
        }

    }

}
