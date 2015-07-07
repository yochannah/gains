package com.digitalcranberry.gainsl.model.events.map;

import org.osmdroid.util.GeoPoint;

/**
 * Created by yo on 05/07/15.
 */
public class SelectCacheArea {
    public GeoPoint point;
    public SelectCacheArea(GeoPoint p){
        this.point = p;
    }
}
