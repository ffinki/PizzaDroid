package com.droid.filip.pizzadroid.singletons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonSingleton {

    private static GsonSingleton singleton;
    private Gson gson;


    private GsonSingleton() {
        gson = new GsonBuilder().create();
    }

    public static Gson getInstance() {
        if (singleton == null)
            singleton = new GsonSingleton();
        return singleton.getGson();
    }

    public Gson getGson() {
        return gson;
    }
}
