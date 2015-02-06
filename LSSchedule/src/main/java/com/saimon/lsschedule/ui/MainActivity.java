package com.saimon.lsschedule.ui;

import android.accounts.Account;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.saimon.lsschedule.BuildConfig;
import com.saimon.lsschedule.Constants;
import com.saimon.lsschedule.R;
import com.saimon.lsschedule.provider.LSDatabase;
import com.saimon.lsschedule.sync.SyncAccountManager;
import com.saimon.lsschedule.ui.fragment.GroupsFragment;
import com.saimon.lsschedule.ui.fragment.ScheduleGroupsFragment;
import com.saimon.lsschedule.util.DatabaseUtils;
import com.saimon.lsschedule.util.InstallationUtils;

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener {
    public static final String TAG = "MainActivity";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    // Sync interval constants
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long MINUTES_PER_HOUR = 60L;
    public static final long SYNC_INTERVAL_IN_HOURS = 6L;
    public static final long SYNC_INTERVAL =
                    SYNC_INTERVAL_IN_HOURS *
                    MINUTES_PER_HOUR *
                    SECONDS_PER_MINUTE;

    private static final String CONF_ARG_SELECTED_NAV_DRAWER_ITEM_INDEX = "nav_drawer_item_index";
    private static final String CONF_ARG_SELECTED_GROUP_CATEGORY_INDEX = "group_category_index";

    private static final int NAV_ITEM_INDEX_SCHEDULE = 0;
    private static final int NAV_ITEM_INDEX_GROUPS = 1;

    private DrawerLayout mDrawerLayout;
    private LinearLayout mLeftDrawer;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

//    private Spinner mGroupsSpinner;
//    private CursorAdapter mGroupsAdapter;

//    private ToggleButton mFlashLightToggleBtn;
//    private Camera mCamera;
//    private SurfaceView mCameraSurfaceView;
//    private boolean mIsFlashLightOn;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mNavigationItemTitles;

    private int mSelectedNavDrawerItemIndex = -1;
    private int mSelectedGroupCategoryIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        DatabaseUtils.exportDatabase(getApplicationContext().getPackageName(), LSDatabase.DATABASE_NAME);

        if (savedInstanceState != null) {
            mSelectedNavDrawerItemIndex = savedInstanceState.getInt(CONF_ARG_SELECTED_NAV_DRAWER_ITEM_INDEX);
            mSelectedGroupCategoryIndex = savedInstanceState.getInt(CONF_ARG_SELECTED_GROUP_CATEGORY_INDEX);
        }

        mTitle = mDrawerTitle = getTitle();
        mNavigationItemTitles = getResources().getStringArray(R.array.nav_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLeftDrawer = (LinearLayout) findViewById(R.id.left_drawer);
        mDrawerList = (ListView) findViewById(R.id.left_drawer_list);
//        mGroupsSpinner = (Spinner) findViewById(R.id.groups_spinner);
//        mFlashLightToggleBtn = (ToggleButton) findViewById(R.id.flashlight_toggle_btn);
//        mCameraSurfaceView = (SurfaceView)findViewById(R.id.camera_surface_view);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mNavigationItemTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

//        getSupportActionBar().setBackgroundDrawable(
//                new ColorDrawable(getResources().getColor(R.color.tab_indicator)));

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                if (mSelectedNavDrawerItemIndex == NAV_ITEM_INDEX_GROUPS) {
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                    getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
                } else {
                    getSupportActionBar().setDisplayShowTitleEnabled(true);
                    setTitle(mNavigationItemTitles[mSelectedNavDrawerItemIndex]);
                }
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                getSupportActionBar().setTitle(mDrawerTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

                EasyTracker easyTracker = EasyTracker.getInstance(MainActivity.this);
                easyTracker.send(MapBuilder
                        .createEvent("ui_action",
                                     "sliding_menu_open",
                                     null, null)
                        .build()
                );
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        } else {
            if (mSelectedNavDrawerItemIndex == NAV_ITEM_INDEX_GROUPS) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
                getSupportActionBar().setListNavigationCallbacks(
                        ArrayAdapter.createFromResource(this, R.array.group_categories,
                                android.R.layout.simple_spinner_dropdown_item),
                        this);

                getSupportActionBar().setSelectedNavigationItem(mSelectedGroupCategoryIndex);
            }
        }

        Account account = SyncAccountManager.createSyncAccount(this);
        // This must be called in order to get the periodic sync working. Weird or what?
        ContentResolver.setSyncAutomatically(account, Constants.ContentProvider.AUTHORITY, true);
        // Turn on periodic sync
        ContentResolver.addPeriodicSync(
                account,
                Constants.ContentProvider.AUTHORITY,
                new Bundle(),
                SYNC_INTERVAL);

        if (InstallationUtils.isFirstLaunch(this)) {
            mDrawerLayout.openDrawer(mLeftDrawer);
            InstallationUtils.setFirstLaunchComplete(this);
        }


//        if (!CameraUtils.isCameraFlashSupported(this)) {
//            mFlashLightToggleBtn.setVisibility(View.GONE);
//        } else {
//            mCamera = Camera.open();
//            final Camera.Parameters cameraParams = mCamera.getParameters();
//
//            mFlashLightToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                    if (isChecked) {
//                        cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//                        try {
//                            mCamera.setPreviewDisplay(mCameraSurfaceView.getHolder());
//                            mCamera.setParameters(cameraParams);
//                            mCamera.startPreview();
//                            mIsFlashLightOn = true;
//                        } catch (IOException e) {
//                            Toast.makeText(MainActivity.this, R.string.flashlight_error_msg, Toast.LENGTH_SHORT).show();
//
//                            mFlashLightToggleBtn.setChecked(false);
//                            mIsFlashLightOn = false;
//                        }
//                    } else {
//                        cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
//                        mCamera.setParameters(cameraParams);
//                        mCamera.stopPreview();
//
//                        mIsFlashLightOn = false;
//                    }
//                }
//           });
//        }
    }

//    private void turnOnFlashLight() {
//
//    }

    @Override
    protected void onResume() {
        super.onResume();

        // The onDrawerOpened() callback is not called after an orientation change when
        // the drawer is open in the previous orientation.
        // We want to make sure that the app title is displayed when the drawer is open.
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mLeftDrawer);
        if (DEBUG) Log.d(TAG, "isDrawerOpen: " + drawerOpen);
        if (drawerOpen) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            getSupportActionBar().setTitle(mDrawerTitle);
        }

        // http://stackoverflow.com/questions/11293441/android-loadercallbacks-onloadfinished-called-twice
//        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mLeftDrawer);
//        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
            case R.id.action_about:
                DialogFragment aboutDialogFragment = AboutDialogFragment.newInstance();
                aboutDialogFragment.show(getSupportFragmentManager(), "about_dialog");

                EasyTracker easyTracker = EasyTracker.getInstance(this);
                easyTracker.send(MapBuilder
                        .createEvent("ui_action",
                                     "option_press",
                                     "About",
                                     null)
                        .build()
                );

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);

            EasyTracker easyTracker = EasyTracker.getInstance(MainActivity.this);
            easyTracker.send(MapBuilder
                    .createEvent(
                            "ui_action",
                            "menu_press",
                            position == 0 ? "Schedule" : "Groups",
                            null)
                    .build()
            );
        }
    }

    private void selectItem(int position) {
        if (DEBUG) Log.d(TAG, "selectItem() called. position: " + position);
        if (DEBUG) Log.d(TAG, "mSelectedNavDrawerItemIndex: " + mSelectedNavDrawerItemIndex);

        if (mSelectedNavDrawerItemIndex != position) {
            switch (position) {
                case NAV_ITEM_INDEX_SCHEDULE:
                    getSupportActionBar().setDisplayShowTitleEnabled(true);
                    getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

                    Fragment fragment = ScheduleGroupsFragment.newInstance();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                    break;
                case NAV_ITEM_INDEX_GROUPS:
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                    getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
                    // This should only be called once in onCreate() but the navigation callbacks
                    // are not fired unless the callbacks are registered every time when the
                    // navigation mode changes.
                    getSupportActionBar().setListNavigationCallbacks(
                            ArrayAdapter.createFromResource(this, R.array.group_categories,
                                    android.R.layout.simple_spinner_dropdown_item),
                            this);
                    break;
            }
        }
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);

        setTitle(mNavigationItemTitles[position]);
        mDrawerLayout.closeDrawer(mLeftDrawer);

        mSelectedNavDrawerItemIndex = position;
    }

//    private void setUpListNavigationModeForGroups() {
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

//        List<String> items = new ArrayList<String>();
//        items.add(getString(R.string.groups_in_ktm));
//        items.add(getString(R.string.groups_outside_ktm));
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, android.R.id.text1, items);
//        getSupportActionBar().setListNavigationCallbacks(adapter, this);
//    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }


    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CONF_ARG_SELECTED_NAV_DRAWER_ITEM_INDEX, mSelectedNavDrawerItemIndex);
        outState.putInt(CONF_ARG_SELECTED_GROUP_CATEGORY_INDEX, mSelectedGroupCategoryIndex);
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if (DEBUG) Log.d(TAG, "onNavigationItemSelected() called. itemPosition: " + itemPosition + getSupportFragmentManager().findFragmentByTag("fragment_" + itemPosition));

        String fragmentTag = "fragment_" + itemPosition;
        // to prevent fragment re-selection and loosing early saved state
        if (getSupportFragmentManager().findFragmentByTag(fragmentTag) != null) {
            return true;
        }

        boolean inKtm = itemPosition == 0;
        Fragment fragment = GroupsFragment.newInstance(inKtm);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, fragmentTag).commit();

        mSelectedGroupCategoryIndex = itemPosition;

        EasyTracker easyTracker = EasyTracker.getInstance(this);
        easyTracker.send(MapBuilder
                .createEvent(
                        "ui_action",
                        "navigation_press",
                        inKtm ? "Groups in KTM" : "Groups outside KTM",
                        null)
                .build()
        );

        return true;
    }

    //==========================================================
    // Loader Callbacks
    //==========================================================

//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        return new CursorLoader(this,
//                LSContract.Group.CONTENT_URI, null, null, null,
//                LSContract.Group.NAME);
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
//        if (DEBUG) Log.d(TAG, "Loaded " + cursor.getCount() + " groups.");
//        cursor.moveToFirst();
//
//        if (mGroupsSpinner.getAdapter() == null) {
//            mGroupsAdapter = new SimpleCursorAdapter(this,
//                    R.layout.support_simple_spinner_dropdown_item,
//                    cursor,
//                    new String[]{LSContract.Group.NAME},
//                    new int[] {android.R.id.text1});
//            mGroupsSpinner.setAdapter(mGroupsAdapter);
//        } else {
//            mGroupsAdapter.swapCursor(cursor);
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> cursorLoader) {
//        mGroupsAdapter.swapCursor(null);
//    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }


    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    public static class AboutDialogFragment extends DialogFragment {

        public static AboutDialogFragment newInstance() {
            return new AboutDialogFragment();
        }

        public AboutDialogFragment() {}

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.action_about)
                    .setMessage(Html.fromHtml(getString(R.string.about_msg)))

//                    .setPositiveButton(R.string.alert_dialog_ok,
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int whichButton) {
//                                    ((FragmentAlertDialog)getActivity()).doPositiveClick();
//                                }
//                            }
//                    )
//                    .setNegativeButton(R.string.alert_dialog_cancel,
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int whichButton) {
//                                    ((FragmentAlertDialog)getActivity()).doNegativeClick();
//                                }
//                            }
//                    )
                    .create();
        }
    }

}
