package com.sillylife.bugreporter.shakedetector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {

    // g-force required to register a shake event
    private static final float SHAKE_THRESHOLD = 2.0F;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;
    private static final int MIN_SHAKE_COUNT = 2;

    private final OnShakeListener mOnShakeListener;
    private long mShakeTimestamp;
    private int mShakeCount;

    public ShakeDetector(OnShakeListener onShakeListener) {
        mOnShakeListener = onShakeListener;
    }

    public interface OnShakeListener {
        void onShake();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;

        float gForce = (float)Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        if (gForce > SHAKE_THRESHOLD) {
            final long now = System.currentTimeMillis();
            // ignore shake events too close to each other
            if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                return;
            }

            // reset the shake count after several seconds of no shakes
            if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                mShakeCount = 0;
            }

            mShakeTimestamp = now;
            mShakeCount++;

            if (mShakeCount >= MIN_SHAKE_COUNT) {
                mShakeCount = 0;
                mOnShakeListener.onShake();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
