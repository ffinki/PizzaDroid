package com.droid.filip.pizzadroid.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class PizzaMapFragment extends SupportMapFragment implements OnMapReadyCallback {

    private GoogleMap map = null;

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
        setRetainInstance(true);
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
        if (map != null)
            map.setMyLocationEnabled(false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        doWhenMapIsReady();
    }

    @SuppressLint("MissingPermission")
    void doWhenMapIsReady() {
        if (map != null && isResumed())
           map.setMyLocationEnabled(true);
    }
}
