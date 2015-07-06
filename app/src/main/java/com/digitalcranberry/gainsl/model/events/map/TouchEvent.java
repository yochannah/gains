package com.digitalcranberry.gainsl.model.events.map;

import org.osmdroid.util.GeoPoint;

/**
 * Created by yo on 05/07/15.
 */
public class TouchEvent {
    public GeoPoint point;
    public TouchEvent(GeoPoint p){
        this.point = p;
    }
}
