package com.droid.filip.pizzadroid.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class MyPlaceRefresherService extends IntentService {

    public MyPlaceRefresherService() {
        super("com.droid.filip.pizzadroid.services.MyPlaceRefresherService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStart(intent, startId);
        return START_NOT_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Intent broadcastIntent = intent.getParcelableExtra("CALLING_INTENT");
        handleBroadcastIntent(broadcastIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void handleBroadcastIntent(Intent broadcastIntent) {
        try {
            //do the work here
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
