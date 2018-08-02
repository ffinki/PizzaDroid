package com.droid.filip.pizzadroid.services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.droid.filip.pizzadroid.listeners.PlaceBufferCompleteListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MyPlaceRefresherService extends IntentService {

    private Context context;
    private LatLng latLng;
    private LatLngBounds visibleRegion;
    List<Place> pizzaPlaces;
    //

    public MyPlaceRefresherService() {
        super("com.droid.filip.pizzadroid.services.MyPlaceRefresherService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStart(intent, flags);
        return START_NOT_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Intent broadcastIntent = intent.getParcelableExtra("CALLING_INTENT");
        String googleApiKey = intent.getStringExtra("GOOGLE_DM_API_KEY") != null ?
                intent.getStringExtra("GOOGLE_DM_API_KEY") : null;
        String userAgent = intent.getStringExtra("USER_AGENT") != null ?
                intent.getStringExtra("USER_AGENT") : null;
        visibleRegion = visibleRegion == null ?
                (LatLngBounds)intent.getParcelableExtra("LAT_LNG_BOUNDS") : visibleRegion;
        handleBroadcastIntent(broadcastIntent, googleApiKey, userAgent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void handleBroadcastIntent(Intent broadcastIntent, String googleApiKey, String userAgent) {
        try {
            //do the work here
            //Log.d("COUNTERBROADCAST", "Working every minute");
            getLatLng();
            Thread.sleep(1000*60);
            getPizzaRestaurants(googleApiKey, userAgent);
            //sending the broadcast to implement the repeteable service
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLatLng() {
        FusedLocationProviderClient locator = LocationServices
                .getFusedLocationProviderClient(context);
        Task<Location> myLocation = locator.getLastLocation();
        myLocation.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (task.isSuccessful() && location != null) {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                }
            }
        });
    }

    private void sendBroadcastWithTheNewLocation() {
        //to be implemented
    }

    private void getPizzaRestaurants(final String googleApiKey, final String userAgent) {
        Task<AutocompletePredictionBufferResponse> predictionsBufferTask = Places.getGeoDataClient(context)
                .getAutocompletePredictions(
                        "pizza",
                        visibleRegion,
                        GeoDataClient.BoundsMode.STRICT,
                        null);
        predictionsBufferTask.addOnCompleteListener(new OnCompleteListener<AutocompletePredictionBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<AutocompletePredictionBufferResponse> task) {
                AutocompletePredictionBufferResponse predictionsBuffer = task.getResult();
                if (task.isSuccessful() && predictionsBuffer != null) {
                    pizzaPlaces = new ArrayList<>();
                    for (int i = 0; i < predictionsBuffer.getCount(); i++) {
                        AutocompletePrediction prediction = predictionsBuffer.get(i).freeze();
                        Task<PlaceBufferResponse> placeBufferTask = Places.getGeoDataClient(context)
                                .getPlaceById(prediction.getPlaceId());
                        placeBufferTask.addOnCompleteListener(new PlaceBufferCompleteListener(context,
                                i, predictionsBuffer.getCount(), pizzaPlaces, latLng,
                                googleApiKey, userAgent));
                        if (i == predictionsBuffer.getCount() && prediction.isDataValid())
                            predictionsBuffer.release();
                    }
                }
            }});
    }
}
