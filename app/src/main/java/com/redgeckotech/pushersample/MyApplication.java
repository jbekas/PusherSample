package com.redgeckotech.pushersample;

import android.app.Application;

import com.redgeckotech.pushersample.util.Utilities;

import timber.log.Timber;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        // initialize pusher service
        Utilities.getPusherService(this);
    }
}
