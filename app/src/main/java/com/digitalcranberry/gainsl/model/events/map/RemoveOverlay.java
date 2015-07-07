package com.digitalcranberry.gainsl.model.events.map;

import org.osmdroid.views.overlay.Overlay;

/**
 * Created by yo on 06/07/15.
 */
public class RemoveOverlay {
    public final Overlay overlay;

    public RemoveOverlay(Overlay o){
        this.overlay = o;
    }
}
