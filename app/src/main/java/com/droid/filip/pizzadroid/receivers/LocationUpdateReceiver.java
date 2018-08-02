package com.droid.filip.pizzadroid.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.droid.filip.pizzadroid.services.MyPlaceRefresherService;
import com.droid.filip.pizzadroid.stakes.distancematrixapi.DistanceMatrixResponse;
import com.droid.filip.pizzadroid.stakes.distancematrixapi.Element;
import com.droid.filip.pizzadroid.stakes.distancematrixapi.Row;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.ArrayList;

public class LocationUpdateReceiver extends BroadcastReceiver {

    private GoogleMap map;

    public LocationUpdateReceiver() {}

    public LocationUpdateReceiver(GoogleMap map) {
        this();
        this.map = map;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("COUNTERBROADCAST", "In the receiver...");
        DistanceMatrixResponse distanceMatrixResponse = (DistanceMatrixResponse)intent.getSerializableExtra("DISTANCES_RESPONSE");
        ArrayList<Place> places = (ArrayList<Place>)intent.getSerializableExtra("PLACES");
        Row originDestinations = distanceMatrixResponse.getRows()[0];
        Element[] distances = originDestinations.getElements();
        for (int i=0; i<distances.length; i++) {
            Log.d("DISTANCE", String.valueOf(distances[i].getDuration().getValue()));
            if (distances[i].getDuration().getValue()/60 < 11) {
                MarkerOptions opt = new MarkerOptions()
                        .draggable(false)
                        .flat(true)
                        .position(places.get(i).getLatLng())
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_RED))
                        .title(distanceMatrixResponse.getDestination_addresses()[i]);
                map.addMarker(opt);
            }
        }
        callTheService(context, intent);
    }

    private void callTheService(Context context, Intent intent) {
        Intent i = new Intent(context, MyPlaceRefresherService.class);
        i.putExtra("CALLING_INTENT", intent);
        context.startService(i);
    }
}
