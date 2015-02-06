package com.saimon.lsschedule.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.saimon.lsschedule.BuildConfig;
import com.saimon.lsschedule.R;


/**
 * Based on Android-ProgressFragment but with support for displaying error message and retry button.
 * https://github.com/johnkil/Android-ProgressFragment/blob/master/progressfragment/src/com/devspark/progressfragment/ProgressFragment.java
 *
 */
public abstract class ProgressFragment extends Fragment {
    private static final String TAG = "ProgressFragment";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final String CONF_ARG_PROGRESS_SHOWN = "progress_shown";
    private static final String CONF_ARG_CONTENT_SHOWN = "content_shown";
    private static final String CONF_ARG_ERROR_SHOWN = "error_shown";

    private ViewGroup mProgressContainer;
    private ViewGroup mContentContainer;
    private ViewGroup mErrorContainer;

    private View mContentView;
    private Button mRetryBtn;

    private boolean mProgressShown;
    private boolean mContentShown;
    private boolean mErrorShown;

    public ProgressFragment() {
        // Required empty public constructor
    }

    public boolean isProgressShown() {
        return mProgressShown;
    }

    public boolean isContentShown() {
        return mContentShown;
    }

    public boolean isErrorShown() {
        return mErrorShown;
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (DEBUG) Log.d(TAG, "onViewCreated()");

        mProgressContainer = (ViewGroup) view.findViewById(R.id.progress_container);
        mContentContainer = (ViewGroup) view.findViewById(R.id.content_container);
        mErrorContainer = (ViewGroup) view.findViewById(R.id.error_container);

        mRetryBtn = (Button) mErrorContainer.findViewById(R.id.error_retry_btn);
        mRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRetryButtonClicked(view);
            }
        });

        View contentView = onCreateContentView(LayoutInflater.from(getActivity()), savedInstanceState);
        setContentView(contentView);
    }

    /**
     * Return the content view
     * @param inflater Layout Inflater
     * @param savedInstanceState Saved instance state
     * @return Content View
     */
    public abstract View onCreateContentView(LayoutInflater inflater,
                                             Bundle savedInstanceState);

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (DEBUG) Log.d(TAG, "onActivityCreated()-> savedInstanceState: " + savedInstanceState);

        if (savedInstanceState == null) {
            // show the progress view initially
            mContentContainer.setVisibility(View.GONE);
            mErrorContainer.setVisibility(View.GONE);
            showProgress(false);
        } else {
            if (savedInstanceState.getBoolean(CONF_ARG_PROGRESS_SHOWN)) {
                showProgress(false);
            } else if (savedInstanceState.getBoolean(CONF_ARG_CONTENT_SHOWN)) {
                showContent(false);
            } else if (savedInstanceState.getBoolean(CONF_ARG_ERROR_SHOWN)) {
                showError(false);
            }
        }
    }

    public void setContentView(View view) {
        if (DEBUG) Log.d(TAG, "setContentView()");

        if (view == null) {
            throw new IllegalArgumentException("Content view can't be null");
        }

        if (mContentView == null) {
            mContentContainer.addView(view);
        } else {
            int index = mContentContainer.indexOfChild(mContentView);
            // replace content view
            mContentContainer.removeView(mContentView);
            mContentContainer.addView(view, index);
        }
        mContentView = view;
    }

    public View getContentView() {
        return mContentView;
    }

    @Override
    public void onDestroyView() {
        mProgressContainer = mContentContainer = mErrorContainer = null;
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(CONF_ARG_PROGRESS_SHOWN, mProgressShown);
        outState.putBoolean(CONF_ARG_CONTENT_SHOWN, mContentShown);
        outState.putBoolean(CONF_ARG_ERROR_SHOWN, mErrorShown);
        super.onSaveInstanceState(outState);
    }

    protected void showProgress() {
        showProgress(false);
    }

    protected void showProgress(boolean animate) {
        if (DEBUG) Log.d(TAG, "showProgress()");
        if (DEBUG) Log.d(TAG, "mProgressShown: " + mProgressShown + ", mContentShown: " + mContentShown + ", mErrorShown: " + mErrorShown);

        if (mProgressShown) {
            return;
        }
        mProgressShown = true;

        if (mContentShown) {
            if (animate) {
                mContentContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            } else {
                mContentContainer.clearAnimation();
                mProgressContainer.clearAnimation();
            }
            mContentContainer.setVisibility(View.GONE);
            mProgressContainer.setVisibility(View.VISIBLE);
        } else if (mErrorShown) {
            if (animate) {
                mErrorContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            } else {
                mErrorContainer.clearAnimation();
                mProgressContainer.clearAnimation();
            }
            mErrorContainer.setVisibility(View.GONE);
            mProgressContainer.setVisibility(View.VISIBLE);
        } else {
            mProgressContainer.setVisibility(View.VISIBLE);
            mContentContainer.setVisibility(View.GONE);
            mErrorContainer.setVisibility(View.GONE);
        }
       mContentShown = mErrorShown = false;
    }

    protected void showContent() {
        showContent(false);
    }

    protected void showContent(boolean animate) {
        if (DEBUG) Log.d(TAG, "showContent()");
        if (DEBUG) Log.d(TAG, "mProgressShown: " + mProgressShown + ", mContentShown: " + mContentShown + ", mErrorShown: " + mErrorShown);

        if (mContentShown) {
            return;
        }
        mContentShown = true;

        if (mProgressShown) {
            if (animate) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
                mContentContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            } else {
                mProgressContainer.clearAnimation();
                mContentContainer.clearAnimation();
            }
            mProgressContainer.setVisibility(View.GONE);
            mContentContainer.setVisibility(View.VISIBLE);
        } else if (mErrorShown) {
            if (animate) {
                mErrorContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
                mContentContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            } else {
                mErrorContainer.clearAnimation();
                mContentContainer.clearAnimation();
            }
            mErrorContainer.setVisibility(View.GONE);
            mContentContainer.setVisibility(View.VISIBLE);
        } else {
            mProgressContainer.setVisibility(View.GONE);
            mContentContainer.setVisibility(View.VISIBLE);
            mErrorContainer.setVisibility(View.GONE);
        }
        mProgressShown = mErrorShown = false;
    }

    protected void showError() {
        showError(false);
    }

    protected void showError(boolean animate) {
        if (DEBUG) Log.d(TAG, "showError()");
        if (DEBUG) Log.d(TAG, "mProgressShown: " + mProgressShown + ", mContentShown: " + mContentShown + ", mErrorShown: " + mErrorShown);

        if (mErrorShown) {
            return;
        }
        mErrorShown = true;

        if (mContentShown) {
            if (animate) {
                mContentContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
                mErrorContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            } else {
                mContentContainer.clearAnimation();
                mErrorContainer.clearAnimation();
            }
            mContentContainer.setVisibility(View.GONE);
            mErrorContainer.setVisibility(View.VISIBLE);
        } else if (mProgressShown) {
            if (animate) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
                mErrorContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            } else {
                mProgressContainer.clearAnimation();
                mErrorContainer.clearAnimation();
            }
            mProgressContainer.setVisibility(View.GONE);
            mErrorContainer.setVisibility(View.VISIBLE);
        } else {
            mProgressContainer.setVisibility(View.GONE);
            mContentContainer.setVisibility(View.GONE);
            mErrorContainer.setVisibility(View.VISIBLE);
        }
        mProgressShown = mContentShown = false;
    }

    protected void onRetryButtonClicked(View view) {}

}
