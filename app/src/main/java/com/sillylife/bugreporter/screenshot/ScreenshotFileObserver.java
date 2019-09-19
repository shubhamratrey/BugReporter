package com.sillylife.bugreporter.screenshot;

import android.app.Activity;
import android.os.Environment;
import android.os.FileObserver;

import java.io.File;

public class ScreenshotFileObserver extends FileObserver implements ScreenshotObserver {
    private static final String PATH = Environment.getExternalStorageDirectory().toString() + "/Pictures/Screenshots/";

    private String mLastTakenPath;
    private final OnScreenshotTakenListener mListener;

    public ScreenshotFileObserver(OnScreenshotTakenListener listener) {
        super(PATH, FileObserver.CLOSE_WRITE);
        mListener = listener;
    }

    @Override
    public void onEvent(int event, String path) {
        if (path == null || event != FileObserver.CLOSE_WRITE) {
            //Log.i(TAG, "Not important");
        } else if (mLastTakenPath != null && path.equalsIgnoreCase(mLastTakenPath)) {
            //Log.i(TAG, "This event has been observed before.");
        } else {
            mLastTakenPath = path;
            File file = new File(PATH + path);

            if (mListener != null) {
                mListener.onScreenshotTaken(file);
            }
        }
    }

    @Override
    public void start(Activity currentActivity, ScreenshotObserverPermissionListener permissionListener) {
        startWatching();
    }

    @Override
    public void stop() {
        stopWatching();
    }
}
