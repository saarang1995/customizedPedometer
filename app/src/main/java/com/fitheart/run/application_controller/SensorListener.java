package com.fitheart.run.application_controller;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class SensorListener extends Service implements SensorEventListener {
    private final static long MICROSECONDS_IN_ONE_MINUTE = 6000;
    private String TAG = "Sensor Manger";
    private final IBinder mBinder = new SensorBinder();
    private float value;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }



    public class SensorBinder extends Binder {
        public SensorListener getService() {
            return SensorListener.this;
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        value=event.values[0];
        SensorData.setValue(value);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public float currentValue(){
        return SensorData.getValue();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        reRegisterSensor();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            sm.unregisterListener(this);
        } catch (Exception ex) {
            Log.e(TAG, "onDestroy: ", ex);
            ex.printStackTrace();
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    private void reRegisterSensor() {

        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER), SensorManager.SENSOR_DELAY_NORMAL, (int) (5 * MICROSECONDS_IN_ONE_MINUTE));
    }



    public static class SensorData {
        private static float inner_value;
        public static float getValue() {
            return inner_value;
        }

        public static void setValue(float value) {
            inner_value = value;
        }

    }

}
