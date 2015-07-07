package com.digitalcranberry.gainsl.model.events.map;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Overlay;

/**
 * Created by yo on 05/07/15.
 */
public class AddOverlay {
    public final Overlay overlay;

    public AddOverlay(Overlay o){
        this.overlay = o;
    }
}
