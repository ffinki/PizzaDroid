package com.droid.filip.pizzadroid.listeners;

import android.content.Context;
import android.support.annotation.NonNull;
import android.webkit.WebView;

import com.droid.filip.pizzadroid.R;
import com.droid.filip.pizzadroid.tasks.DistanceMatrixTask;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class PlaceBufferCompleteListener implements OnCompleteListener<PlaceBufferResponse> {

    private Context context;
    private int i;
    private int n;
    private List<Place> pizzaPlaces;
    private LatLng latLng;
    //
    private String googleApiKey;
    private String userAgent;

    public PlaceBufferCompleteListener(Context context, int i, int n, List<Place> pizzaPlaces, LatLng latLng
    , String googleApiKey, String userAgent) {
        this.context = context;
        this.i = i;
        this.n = n;
        this.pizzaPlaces = pizzaPlaces;
        this.latLng = latLng;
        this.googleApiKey = googleApiKey;
        this.userAgent = userAgent;
    }

    @Override
    public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
        PlaceBufferResponse response = task.getResult();
        if (task.isSuccessful() && response != null) {
            Place place = response.get(0).freeze();
            if (isPizzaPlace(place.getPlaceTypes())) {
                pizzaPlaces.add(place);
                if (i == n - 1 && place.isDataValid()) {
                    fillTheMapWithPizzas();
                }
            }
        }
        response.release();

    }

    private boolean isPizzaPlace(List<Integer> placeTypes) {
        return placeTypes.contains(Place.TYPE_FOOD) || placeTypes.contains(Place.TYPE_RESTAURANT);
    }

    private void fillTheMapWithPizzas() {
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?";
        StringBuilder originDestination = new StringBuilder();
        originDestination.append("origins=" + latLng.latitude + "," + latLng.longitude + "&");
        originDestination.append("destinations=");
        for (int i=0; i<pizzaPlaces.size(); i++) {
            LatLng placeLatLng = pizzaPlaces.get(i).getLatLng();
            originDestination.append(placeLatLng.latitude + "," + placeLatLng.longitude);
            if (i < pizzaPlaces.size() - 1)
                originDestination.append("|");
        }
        originDestination.append("&mode=walking");
        String params = originDestination.toString();
        String apiKey = "key=" + googleApiKey;
        new DistanceMatrixTask(context, (ArrayList<Place>)pizzaPlaces).execute(url, apiKey, params, userAgent);
    }

}
