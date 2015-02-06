package com.saimon.lsschedule.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.saimon.lsschedule.BuildConfig;

/**
 * Created at 4:32 PM on 1/4/14
 * Copyright 2013 Poolsidelabs Inc.
 *
 * @author Saimon Rai
 */
public class LifecycleTaggerFragment extends Fragment {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "LifecycleTaggerFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (DEBUG) Log.i(TAG, "onCreate() called.");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (DEBUG) Log.i(TAG, "onCreateView() called.");
        TextView tv = new TextView(getActivity());
        tv.setTextSize(20);
        tv.setText(TAG);
        return tv;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (DEBUG) Log.i(TAG, "onViewCreated() called.");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (DEBUG) Log.i(TAG, "onActivityCreated() called.");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        if (DEBUG) Log.i(TAG, "onStart() called.");
        super.onStart();
    }

    @Override
    public void onResume() {
        if (DEBUG) Log.i(TAG, "onResume() called.");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (DEBUG) Log.i(TAG, "onSaveInstanceState() called.");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        if (DEBUG) Log.i(TAG, "onPause() called.");
        super.onPause();
    }

    @Override
    public void onStop() {
        if (DEBUG) Log.i(TAG, "onStop() called.");
        super.onStop();
    }

    @Override
    public void onDetach() {
        if (DEBUG) Log.i(TAG, "onDetach() called.");
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        if (DEBUG) Log.i(TAG, "onDestroyView() called.");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.i(TAG, "onDestroy() called.");
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (DEBUG) Log.i(TAG, "onCreateOptionsMenu() called.");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (DEBUG) Log.i(TAG, "onPrepareOptionsMenu() called.");
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onDestroyOptionsMenu() {
        if (DEBUG) Log.i(TAG, "onDestroyOptionsMenu() called.");
        super.onDestroyOptionsMenu();
    }

    @Override
    public void onAttach(Activity activity) {
        if (DEBUG) Log.i(TAG, "onAttach() called.");
        super.onAttach(activity);
    }
}
