package com.saimon.lsschedule.ui.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.saimon.lsschedule.BuildConfig;
import com.saimon.lsschedule.R;
import com.saimon.lsschedule.model.BetterSchedule;
import com.saimon.lsschedule.model.Schedule;
import com.saimon.lsschedule.provider.LSContract;
import com.saimon.lsschedule.sync.SyncUtils;
import com.saimon.lsschedule.util.DateTimeUtils;
import com.saimon.lsschedule.util.Lists;

import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ScheduleFragment extends ProgressFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = "ScheduleFragment";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final String ARG_GROUP_ID = "group_id";

    private int mGroupId;

    private AbsListView mListView;
    private ScheduleAdapter mAdapter;

    public static ScheduleFragment newInstance(int groupId) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mGroupId = getArguments().getInt(ARG_GROUP_ID);
        }

        mAdapter = new ScheduleAdapter(getActivity(), new ArrayList<ScheduleByDate>());
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListView = (AbsListView) getContentView().findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // http://stackoverflow.com/questions/11293441/android-loadercallbacks-onloadfinished-called-twice
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_schedule, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if (DEBUG) Log.i(TAG, "Refreshing schedule...");
                SyncUtils.forceSync(getActivity());

                EasyTracker easyTracker = EasyTracker.getInstance(getActivity());
                easyTracker.send(MapBuilder
                        .createEvent(
                                "ui_action",
                                "option_press",
                                "Refresh Schedule",
                                null)
                        .build()
                );

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //==========================================================
    // Loader Callbacks
    //==========================================================

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //TODO: add the sort logic
        return new CursorLoader(getActivity(),
                LSContract.Schedule.CONTENT_URI, null,
                LSContract.Schedule.GROUP_ID + "=?", new String[] {Integer.toString(mGroupId)},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (DEBUG) Log.d(TAG, "Loaded " + cursor.getCount() + " schedules for group " + mGroupId + ".");
        cursor.moveToFirst();

        int groupIdColumnIndex = cursor.getColumnIndexOrThrow(LSContract.Schedule.GROUP_ID);
        int weekDayColumnIndex = cursor.getColumnIndexOrThrow(LSContract.Schedule.WEEKDAY);
        int startTimeColumnIndex = cursor.getColumnIndexOrThrow(LSContract.Schedule.START_TIME);
        int endTimeColumnIndex = cursor.getColumnIndexOrThrow(LSContract.Schedule.END_TIME);

        Map<String, List<BetterSchedule>> schedulesByWeekDayMap = new LinkedHashMap<String, List<BetterSchedule>>();
        while (!cursor.isAfterLast()) {
            String weekday = cursor.getString(weekDayColumnIndex);

            Schedule s = new Schedule();
            s.setGroupId(cursor.getInt(groupIdColumnIndex));
            s.setWeekday(weekday);
            s.setStartTime(cursor.getString(startTimeColumnIndex));
            s.setEndTime(cursor.getString(endTimeColumnIndex));

            BetterSchedule betterSchedule = BetterSchedule.fromSchedule(s);

            List<BetterSchedule> schedules = schedulesByWeekDayMap.get(weekday);
            if (schedules != null) {
                schedules.add(betterSchedule);
            } else {
                schedules = Lists.newArrayList();
                schedules.add(betterSchedule);
                schedulesByWeekDayMap.put(weekday, schedules);
            }

            cursor.moveToNext();
        }

        mAdapter.clear();

        // set up the dates for 7 days (entire week)
        Calendar calendar = DateTimeUtils.nepaliCalendar();

        DateFormat weekdayDateFormat = new SimpleDateFormat("EEE");
        weekdayDateFormat.setTimeZone(DateTimeUtils.NEPAL_TIMEZONE);

        for (int i=0; i<7; i++) {
            ScheduleByDate scheduleByDate = new ScheduleByDate();
            scheduleByDate.date = calendar.getTime();

            // get the week day from the calendar
            String weekday = weekdayDateFormat.format(calendar.getTime()).toUpperCase();
            scheduleByDate.weekday = weekday;

            scheduleByDate.schedules = schedulesByWeekDayMap.get(weekday);

            calendar.add(Calendar.DATE, 1);

            mAdapter.add(scheduleByDate);
        }

        showContent();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    //==========================================================
    // list adapter model
    //==========================================================
    private static class ScheduleByDate {
        Date date;
        String weekday;
        List<BetterSchedule> schedules = Lists.newArrayList();
    }

    //==========================================================
    // list adapter
    //==========================================================

    private static class ScheduleAdapter extends ArrayAdapter<ScheduleByDate> {
        private static final DateTimeFormatter sTimeFormatter = DateTimeFormat.forPattern("h:mm a");

        private final LayoutInflater mInflater;

        public ScheduleAdapter(Context context, List<ScheduleByDate> scheduleByWeekday) {
            super(context, -1, scheduleByWeekday);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.row_schedule, null);

                holder = new ViewHolder();
                holder.weekdayTv = (TextView)convertView.findViewById(R.id.weekday_tv);
                holder.dateTv = (TextView)convertView.findViewById(R.id.date_tv);
                holder.scheduleTableLayout = (TableLayout)convertView.findViewById(R.id.schedule_table_layout);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ScheduleByDate scheduleByWeekday = getItem(position);
            holder.weekdayTv.setText(scheduleByWeekday.weekday.toUpperCase());

            holder.scheduleTableLayout.removeAllViews();

            if (scheduleByWeekday.schedules != null) {
                for (BetterSchedule schedule : scheduleByWeekday.schedules) {
                    TableRow row = new TableRow(getContext());

                    TextView tv = new TextView(getContext());
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

                    String startTime = sTimeFormatter.print(schedule.getStartTime());
                    String endTime = sTimeFormatter.print(schedule.getEndTime());

                    Period p = new Period(schedule.getStartTime(), schedule.getEndTime());
                    int hourInterval = p.getHours();
                    if (hourInterval < 0) {
                        hourInterval = 24 + hourInterval;
                    }
//                    int minutesInterval = p.getMinutes();

                    TextView startTimeTv = new TextView(getContext());
                    startTimeTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
                    startTimeTv.setText(startTime);
                    startTimeTv.setGravity(Gravity.RIGHT);
                    row.addView(startTimeTv);

                    TextView dashTv = new TextView(getContext());
                    dashTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
                    dashTv.setText("-");
                    dashTv.setPadding(15, 0, 15, 0);
                    row.addView(dashTv);

                    TextView endTimeTv = new TextView(getContext());
                    endTimeTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
                    endTimeTv.setText(endTime);
                    endTimeTv.setGravity(Gravity.RIGHT);
                    row.addView(endTimeTv);

                    Resources resources = getContext().getResources();
                    String duration = resources.getQuantityString(R.plurals.hours, hourInterval, hourInterval);
//                    if (minutesInterval > 0) {
//                        duration = duration + " " + resources.getQuantityString(R.plurals.minutes, minutesInterval, minutesInterval);
//                    }

                    TextView durationTv = new TextView(getContext());
                    durationTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                    durationTv.setText(duration);
                    durationTv.setGravity(Gravity.RIGHT);
                    row.addView(durationTv);

                    holder.scheduleTableLayout.addView(row);
                }
            }

            if (position == 0 || position == 1) {
                Resources resources = getContext().getResources();
                String[] relativeDays = resources.getStringArray(R.array.relative_days);

                holder.dateTv.setText(relativeDays[position]);
                holder.dateTv.setTextColor(resources.getColor(R.color.relative_day_text));
                holder.dateTv.setTypeface(null, Typeface.BOLD);
            } else {
                holder.dateTv.setText(DateTimeUtils.DATE_FORMAT_MONTH_DAY_YEAR_NEPAL_TZ.format(scheduleByWeekday.date));
                holder.dateTv.setTextColor(Color.BLACK);
                holder.dateTv.setTypeface(null, Typeface.NORMAL);
            }

            return convertView;
        }

        private static class ViewHolder {
            TextView weekdayTv;
            TextView dateTv;
            TableLayout scheduleTableLayout;
        }

    }

}
