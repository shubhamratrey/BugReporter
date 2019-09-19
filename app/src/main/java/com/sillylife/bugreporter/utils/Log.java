package com.sillylife.bugreporter.utils;

public final class Log {
    static final String TAG = "sillylife.bugreporter";

    public static void d(String msg) {
        android.util.Log.d(TAG, msg);
    }

    public static void d(String msg, Throwable throwable) {
        android.util.Log.d(TAG, msg, throwable);
    }

    public static void e(String msg) {
        android.util.Log.e(TAG, msg);
    }

    public static void e(String msg, Throwable throwable) {
        android.util.Log.e(TAG, msg, throwable);
    }

    public static void i(String msg) {
        android.util.Log.i(TAG, msg);
    }

    public static void i(String msg, Throwable throwable) {
        android.util.Log.i(TAG, msg, throwable);
    }

    public static void w(String msg) {
        android.util.Log.w(TAG, msg);
    }

    public static void w(String msg, Throwable throwable) {
        android.util.Log.w(TAG, msg, throwable);
    }
}
