package com.droid.filip.pizzadroid.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

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
        super.onStart(intent, flags);
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
            Log.d("COUNTERBROADCAST", "Working every minute");
            Thread.sleep(1000*60);
            //sending the broadcast to implement the repeteable service
            Intent newBroadcastIntent = new Intent("com.droid.filip.pizzadroid.intents.lur");
            newBroadcastIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            sendBroadcast(newBroadcastIntent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
