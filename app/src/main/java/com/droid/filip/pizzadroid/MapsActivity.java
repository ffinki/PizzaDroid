package com.droid.filip.pizzadroid;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.droid.filip.pizzadroid.fragments.PizzaMapFragment;

public class MapsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_activity);
        //
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        //
        PizzaMapFragment map = (PizzaMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        if (map == null) {
            map = PizzaMapFragment.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.google_map, map).commit();
        }
    }
}
