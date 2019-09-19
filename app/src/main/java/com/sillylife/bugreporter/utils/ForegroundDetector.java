package com.sillylife.bugreporter.utils;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class ForegroundDetector implements Application.ActivityLifecycleCallbacks {
    private Activity mCurrentActivity = null;
    private boolean mForegrounded = false;
    private final OnForegroundListener mOnForegroundListener;

    public interface OnForegroundListener {
        void onForegroundEvent();
        void onBackgroundEvent();
    }

    public ForegroundDetector(Application application, OnForegroundListener onForegroundListener) {
        mOnForegroundListener = onForegroundListener;
        application.registerActivityLifecycleCallbacks(this);
    }

    Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    boolean getForegrounded() {
        return mForegrounded;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        mCurrentActivity = activity;
        mForegrounded = true;
        mOnForegroundListener.onForegroundEvent();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        mForegrounded = false;
        mOnForegroundListener.onBackgroundEvent();
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
