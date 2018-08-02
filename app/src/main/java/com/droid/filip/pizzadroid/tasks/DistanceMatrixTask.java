package com.droid.filip.pizzadroid.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.droid.filip.pizzadroid.singletons.GsonSingleton;
import com.droid.filip.pizzadroid.stakes.distancematrixapi.DistanceMatrixResponse;
import com.droid.filip.pizzadroid.stakes.distancematrixapi.Element;
import com.droid.filip.pizzadroid.stakes.distancematrixapi.Row;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class DistanceMatrixTask extends AsyncTask<String, DistanceMatrixResponse, DistanceMatrixResponse> {

    private Context context;
    private GoogleMap map;
    private ArrayList<Place> places;

    public DistanceMatrixTask(Context context, ArrayList<Place> places) {
        this.context = context;
        this.places = places;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected DistanceMatrixResponse doInBackground(String... strings) {
        String urlString = strings[0];
        String apiKey = strings[1];
        String params = strings[2];
        String userAgent = strings[3];
        URL url = null;
        HttpsURLConnection https = null;
        BufferedReader br = null;
        DistanceMatrixResponse response = null;
        try {
            url = new URL(urlString + params);
            https = (HttpsURLConnection)url.openConnection();
            https.setRequestProperty("User-Agent", userAgent);
            https.setRequestProperty("Accept", "application/json");
            br = new BufferedReader(new InputStreamReader(https.getInputStream(), "UTF-8"));
            response = GsonSingleton.getInstance().fromJson(br, DistanceMatrixResponse.class);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (https != null)
                https.disconnect();
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    @Override
    protected void onPostExecute(DistanceMatrixResponse distanceMatrixResponse) {
        /*Row originDestinations = distanceMatrixResponse.getRows()[0];
        if (originDestinations.getElements().length != places.size())
            return;
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
        }*/
        Intent newBroadcastIntent = new Intent("com.droid.filip.pizzadroid.intents.lur");
        newBroadcastIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        newBroadcastIntent.putExtra("DISTANCES_RESPONSE", distanceMatrixResponse);
        newBroadcastIntent.putExtra("PLACES", places);
        context.sendBroadcast(newBroadcastIntent);

    }
}
