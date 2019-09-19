package com.sillylife.bugreporter.shakedetector;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;

import com.sillylife.bugreporter.utils.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Screenshotter {
    //    private final View mRootView;
    private final List<View> mRootViews;

    public Screenshotter(Activity activity) {
        List<View> temp;
        temp = Screenshotter.getRootViews();
        if (temp == null || temp.size() == 0) {
            temp = new ArrayList<>();
            temp.add(activity.getWindow().getDecorView().getRootView());
        }
        mRootViews = temp;
    }

    public Screenshotter(View view) {
        mRootViews = new ArrayList<>();
        mRootViews.add(view);
    }

    public Bitmap getBitmap() {
        Bitmap flatteningCursor = Screenshotter.getBitmap(mRootViews.get(0));
        for (int i = 1; i < mRootViews.size(); i++) {
            flatteningCursor = Screenshotter.overlay(flatteningCursor, mRootViews.get(i));
        }
        return flatteningCursor;
    }

    // adapted from https://stackoverflow.com/questions/10616777/how-to-merge-to-two-bitmap-one-over-another
    private static Bitmap overlay(Bitmap bmp1, View view2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        Bitmap bmp2 = getBitmap(view2);
        if (bmp2 == null) {
            return bmOverlay;
        }
        int[] xy = new int[2];
        view2.getLocationOnScreen(xy);
        canvas.drawBitmap(bmp2, xy[0], xy[1], null);
        return bmOverlay;
    }

    private static Bitmap getBitmap(View view) {
        boolean drawingCacheWasEnabled = view.isDrawingCacheEnabled();

        if (drawingCacheWasEnabled) {
            Bitmap sourceBitmap = view.getDrawingCache();
            Bitmap copiedBitmap = sourceBitmap.copy(sourceBitmap.getConfig(), false);
            return copiedBitmap;
        } else {
            view.setDrawingCacheEnabled(true);
            Bitmap sourceBitmap = view.getDrawingCache();
            if (sourceBitmap == null) { // I see this occasionally in the debugger, but not live :/
                Log.w("Unable to create bitmap from view: "+ view + " This view will not be part of the screenshot.");
                return null;
            }
            Bitmap copiedBitmap = sourceBitmap.copy(sourceBitmap.getConfig(), false);
            view.setDrawingCacheEnabled(drawingCacheWasEnabled);
            return copiedBitmap;
        }

    }
    // adapted from https://stackoverflow.com/questions/19669984/is-there-a-way-to-programmatically-locate-all-windows-within-a-given-application
    private static List<View> getRootViews() {
        ArrayList<View> rootViews = new ArrayList<>();
        try {
            Class wmgClass = Class.forName("android.view.WindowManagerGlobal");
            Object wmgInstnace = wmgClass.getMethod("getInstance").invoke(null, (Object[])null);

            Method getViewRootNames = wmgClass.getMethod("getViewRootNames");
            Method getRootView = wmgClass.getMethod("getRootView", String.class);
            String[] rootViewNames = (String[])getViewRootNames.invoke(wmgInstnace, (Object[])null);

            for(String viewName : rootViewNames) {
                View rootView = (View)getRootView.invoke(wmgInstnace, viewName);
                rootViews.add(rootView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootViews;
    }

}
