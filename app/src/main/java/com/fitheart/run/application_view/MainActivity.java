package com.fitheart.run.application_view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fitheart.run.R;
import com.fitheart.run.application_controller.SensorListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button startService, stopService;
    private TextView stepsCount;
    private SensorListener sensorListener;
    boolean mBound = false;
    private final Handler mHandler = new Handler();
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;
    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            update_stepCounts();
        }
    };
    private final ScheduledExecutorService mExecutorService =
            Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> mScheduleFuture;

    private void scheduleSeekbarUpdate() {
        if (!mExecutorService.isShutdown()) {
            mScheduleFuture = mExecutorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            mHandler.post(mUpdateProgressTask);
                        }
                    }, PROGRESS_UPDATE_INITIAL_INTERVAL,
                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        Intent intent = new Intent(this, SensorListener.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        startService(intent);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mBound = false;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void init() {

        // init views:

        startService = findViewById(R.id.start_service);
        stopService = findViewById(R.id.stop_service);
        stepsCount = findViewById(R.id.steps_count);

        // init button click listeners:

        startService.setOnClickListener(this);
        stopService.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_service:
                scheduleSeekbarUpdate();
                break;
            case R.id.stop_service:
                unbindService(mConnection);
                break;
        }
    }

    private void update_stepCounts() {

        if (mBound) stepsCount.setText(String.valueOf(sensorListener.currentValue()));
        Toast.makeText(MainActivity.this, "Value updated", Toast.LENGTH_SHORT).show();
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SensorListener.SensorBinder binder = (SensorListener.SensorBinder) service;

            sensorListener = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }

    };
}
