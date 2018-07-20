package com.droid.filip.pizzadroid.stakes.distancematrixapi;

import java.io.Serializable;

public class Element implements Serializable {

    private String status;
    private Description duration;
    private Description distance;

    public Element() {}

    public Element(String status, Description duration, Description distance) {
        this.status = status;
        this.duration = duration;
        this.distance = distance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Description getDuration() {
        return duration;
    }

    public void setDuration(Description duration) {
        this.duration = duration;
    }

    public Description getDistance() {
        return distance;
    }

    public void setDistance(Description distance) {
        this.distance = distance;
    }
}
