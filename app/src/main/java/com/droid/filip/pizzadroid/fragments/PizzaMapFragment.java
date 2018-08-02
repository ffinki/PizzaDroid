package com.droid.filip.pizzadroid.fragments;

import android.annotation.SuppressLint;
import android.arch.core.executor.DefaultTaskExecutor;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.droid.filip.pizzadroid.R;
import com.droid.filip.pizzadroid.receivers.LocationUpdateReceiver;
import com.droid.filip.pizzadroid.services.MyPlaceRefresherService;
import com.droid.filip.pizzadroid.tasks.DistanceMatrixTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class PizzaMapFragment extends SupportMapFragment implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Context context = null;
    private GoogleMap map = null;
    private GoogleApiClient client = null;
    private LatLng latLng = null;
    private List<Place> pizzaPlaces;

    public static PizzaMapFragment newInstance() {
        PizzaMapFragment fragment = new PizzaMapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getMapAsync(this);
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (client == null) {
            setRetainInstance(true);
            context = getActivity().getApplication();
            client = new GoogleApiClient.Builder(context, this, this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
            client.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //take my location and set the map camera around that region
        FusedLocationProviderClient locator = LocationServices
                .getFusedLocationProviderClient(context);
        Task<Location> myLocation = locator.getLastLocation();
        myLocation.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (task.isSuccessful() && location != null) {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    //registering the broadcast receiver
                    LocationUpdateReceiver lur = new LocationUpdateReceiver(map);
                    IntentFilter filter = new IntentFilter("com.droid.filip.pizzadroid.intents.lur");
                    context.registerReceiver(lur, filter);
                    //starting the service
                    Intent serviceIntent = new Intent(context, MyPlaceRefresherService.class);
                    serviceIntent.putExtra("GOOGLE_DM_API_KEY", getResources().getString(R.string.GOOGLE_API_WEB));
                    serviceIntent.putExtra("USER_AGENT", new WebView(getActivity()).getSettings().getUserAgentString());
                    serviceIntent.putExtra("LAT_LNG_BOUNDS", map.getProjection().getVisibleRegion().latLngBounds);
                    context.startService(serviceIntent);
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(context, "Connection suspended", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(context, "Connection failed", Toast.LENGTH_LONG).show();
    }

}