package com.digitalcranberry.gainsl.model.events;

import org.osmdroid.util.GeoPoint;

/**
 * Created by yo on 05/07/15.
 */
public class MapTouchEvent {
    public GeoPoint point;
    public MapTouchEvent(GeoPoint p){
        this.point = p;
    }
}
