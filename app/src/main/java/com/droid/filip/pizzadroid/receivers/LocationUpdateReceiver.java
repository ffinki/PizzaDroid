package com.droid.filip.pizzadroid.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.droid.filip.pizzadroid.services.MyPlaceRefresherService;

public class LocationUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        callTheService(context, intent);
    }

    private void callTheService(Context context, Intent intent) {
        Intent i = new Intent(context, MyPlaceRefresherService.class);
        i.putExtra("CALLING_INTENT", intent);
        context.startService(i);
    }
}
