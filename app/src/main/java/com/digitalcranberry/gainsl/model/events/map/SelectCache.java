package com.digitalcranberry.gainsl.model.events.map;

import org.osmdroid.util.GeoPoint;

/**
 * Created by yo on 05/07/15.
 */
public class SelectCache {
    public GeoPoint point;
    public SelectCache(GeoPoint p){
        this.point = p;
    }
}
