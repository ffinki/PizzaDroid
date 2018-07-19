package com.droid.filip.pizzadroid.fragments;

import android.annotation.SuppressLint;
import android.arch.core.executor.DefaultTaskExecutor;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
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
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

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
        doWhenMapIsReady();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        doWhenMapIsReady();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        FusedLocationProviderClient locator = LocationServices
                .getFusedLocationProviderClient(context);
        Task<Location> myLocation = locator.getLastLocation();
        myLocation.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location loc = task.getResult();
                if (task.isSuccessful() && loc != null) {
                    latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                    doWhenMapIsReady();
                    getPizzaRestaurants();
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

    @SuppressLint("MissingPermission")
    void doWhenMapIsReady() {
        if (map != null && latLng != null && isResumed()) {
            MarkerOptions markerOpt = new MarkerOptions()
                    .draggable(false)
                    .flat(true)
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_AZURE));
            map.addMarker(markerOpt);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }

    }

    void getPizzaRestaurants() {
        if (map != null && latLng != null && isResumed()) {
            Task<AutocompletePredictionBufferResponse> predictions =  Places.getGeoDataClient(context).getAutocompletePredictions(
                    "pizza restaurant",
                    map.getProjection().getVisibleRegion().latLngBounds,
                    null);
            predictions.addOnCompleteListener(new OnCompleteListener<AutocompletePredictionBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<AutocompletePredictionBufferResponse> task) {
                    AutocompletePredictionBufferResponse predictions = task.getResult();
                    if (task.isSuccessful() && predictions != null) {
                        pizzaPlaces = new ArrayList<>();
                        for (int i=0; i<predictions.getCount(); i++) {
                            AutocompletePrediction prediction = predictions.get(i);
                            Task<PlaceBufferResponse> placeResponse =
                                    Places.getGeoDataClient(context).getPlaceById(prediction.getPlaceId());
                            placeResponse.addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                                @Override
                                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                                    PlaceBufferResponse placeBuffer = task.getResult();
                                    if (task.isSuccessful() && placeBuffer != null) {
                                        Place place = placeBuffer.get(0);
                                        if (isPizzaPlace(place.getPlaceTypes()))
                                            pizzaPlaces.add(place);
                                    }

                                }
                            });
                        }
                    }
                }
            });
        }

    }

    boolean isPizzaPlace(List<Integer> placeTypes) {
        return placeTypes.contains(Place.TYPE_FOOD) || placeTypes.contains(Place.TYPE_RESTAURANT);
    }
}
