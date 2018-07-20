package com.droid.filip.pizzadroid.stakes.distancematrixapi;

import java.io.Serializable;

public class Row implements Serializable {

    private Element[] elements;

    public Row() { }

    public Row(Element[] elements) {
        this.elements = elements;
    }

    public Element[] getElements() {
        return elements;
    }

    public void setElements(Element[] elements) {
        this.elements = elements;
    }
}
