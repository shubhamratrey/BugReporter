package com.sillylife.bugreporter.screenshot;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

public interface ScreenshotObserver {
    public void start(Activity currentActivity, ScreenshotObserverPermissionListener permissionListener);

    public void stop();

    interface ScreenshotObserverPermissionListener {
        void onPermissionDenied();
    }

    class Factory {
        private Factory() {/* No instances */}

        public static ScreenshotObserver newInstance(Context context, OnScreenshotTakenListener callback) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return new ScreenshotContentObserver(context, callback);
            } else {
                return new ScreenshotFileObserver(callback);
            }
        }
    }
}
