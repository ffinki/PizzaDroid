package com.droid.filip.pizzadroid.stakes.distancematrixapi;

import java.io.Serializable;

public class Description implements Serializable{

    private long value;
    private String text;

    public Description() {}

    public Description(long value, String text) {
        this.value = value;
        this.text = text;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
