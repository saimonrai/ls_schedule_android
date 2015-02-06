package com.saimon.lsschedule.ui.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.saimon.lsschedule.BuildConfig;
import com.saimon.lsschedule.R;
import com.saimon.lsschedule.model.Area;
import com.saimon.lsschedule.model.Substation;
import com.saimon.lsschedule.provider.LSContract;
import com.saimon.lsschedule.ui.fragment.dummy.DummyContent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class AreaFragment extends ProgressFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = "AreaFragment";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final String ARG_GROUP_ID = "group_id";
    private static final String ARG_IN_KTM = "in_ktm";

    private int mGroupId;
    private boolean mInKtm;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private SubstationAdapter mAdapter;

    public static AreaFragment newInstance(int groupId, boolean inKtm) {
        AreaFragment fragment = new AreaFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_GROUP_ID, groupId);
        args.putBoolean(ARG_IN_KTM, inKtm);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AreaFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mGroupId = getArguments().getInt(ARG_GROUP_ID);
            mInKtm = getArguments().getBoolean(ARG_IN_KTM);
        }

        mAdapter = new SubstationAdapter(getActivity(), new ArrayList<SubstationWithAreas>());
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_area_list, null);
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

    //==========================================================
    // Loader Callbacks
    //==========================================================

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{
                "_id", "name", "name_nep", "in_ktm",
                "substation_id", "substation_name", "substation_name_nep"};
        return new CursorLoader(getActivity(),
                LSContract.Area.buildWithSubstationsUri(), projection,
                LSContract.Area.GROUP_ID + "=?" + " AND area." + LSContract.Area.IN_KTM + "=?",
                new String[] {Integer.toString(mGroupId), mInKtm ? "1" : "0"},
                "substation.name ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (DEBUG) Log.d(TAG, "Loaded " + cursor.getCount() + " areas for group " + mGroupId + ".");
        cursor.moveToFirst();

        // convert the data from cursor to a list of objects for the adapter
        List<SubstationWithAreas> substations = new ArrayList<SubstationWithAreas>();
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();

        int areaIdColumnIndex = cursor.getColumnIndexOrThrow("_id");
        int areaNameColumnIndex = cursor.getColumnIndexOrThrow("name");
        int areaNameNepColumnIndex = cursor.getColumnIndexOrThrow("name_nep");
        int areaInKtmColumnIndex = cursor.getColumnIndexOrThrow("in_ktm");
        int substationIdColumnIndex = cursor.getColumnIndexOrThrow("substation_id");
        int substationNameColumnIndex = cursor.getColumnIndexOrThrow("substation_name");
        int substationNameNepaliColumnIndex = cursor.getColumnIndexOrThrow("substation_name_nep");

        while (!cursor.isAfterLast()) {
            int substationId = cursor.getInt(substationIdColumnIndex);

            Area area = new Area();
            area.setId(cursor.getInt(areaIdColumnIndex));
            area.setName(cursor.getString(areaNameColumnIndex));
            area.setNameInNepali(cursor.getString(areaNameNepColumnIndex));
            area.setInKtm(cursor.getInt(areaInKtmColumnIndex) == 1);
            area.setGroupId(mGroupId);
            area.setSubstationId(substationId);

            SubstationWithAreas substation;

            Integer listIndex = map.get(substationId);
            if (listIndex == null) {
                // Add this substation
                substation = new SubstationWithAreas();
                substation.setId(substationId);
                substation.setName(cursor.getString(substationNameColumnIndex));
                substation.setNameInNepali(cursor.getString(substationNameNepaliColumnIndex));
                substation.setAreas(new ArrayList<Area>());

                substations.add(substation);
                map.put(substationId, substations.size()-1);
            } else {
                substation = substations.get(listIndex);
            }

            substation.getAreas().add(area);

            cursor.moveToNext();
        }

        mAdapter.clear();
        for (SubstationWithAreas substation : substations) {
            mAdapter.add(substation);
        }

        showContent();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    //==========================================================
    // model for list adapter
    //==========================================================

    public static class SubstationWithAreas extends Substation {
        private List<Area> areas;

        public List<Area> getAreas() {
            return areas;
        }

        public void setAreas(List<Area> areas) {
            this.areas = areas;
        }
    }

    //==========================================================
    // list adapter
    //==========================================================

    public static class SubstationAdapter extends ArrayAdapter<SubstationWithAreas> {

        private final LayoutInflater mInflater;

        public SubstationAdapter(Context context, List<SubstationWithAreas> substations) {
            super(context, -1, substations);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.row_substation, null);

                holder = new ViewHolder();
                holder.nameTv = (TextView)convertView.findViewById(R.id.name_tv);
                holder.areasLinearLayout = (LinearLayout)convertView.findViewById(R.id.area_list_layout);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            SubstationWithAreas substation = getItem(position);

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                holder.nameTv.setText(substation.getName().toUpperCase() + " ( " + substation.getNameInNepali() + " )");
            } else {
                // nepali fonts are not supported on devices 2.3 and below
                holder.nameTv.setText(substation.getName().toUpperCase());
            }

            holder.areasLinearLayout.removeAllViews();
            for (Area area : substation.getAreas()) {
                TextView tv = new TextView(getContext());
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                    tv.setText("• " + area.getName() + " ( " + area.getNameInNepali() + " )");
                } else {
                    // nepali fonts are not supported on devices 2.3 and below
                    tv.setText("• " + area.getName());
                }
                holder.areasLinearLayout.addView(tv);
            }

            return convertView;
        }

        private static class ViewHolder {
            TextView nameTv;
            LinearLayout areasLinearLayout;
        }
    }

}
