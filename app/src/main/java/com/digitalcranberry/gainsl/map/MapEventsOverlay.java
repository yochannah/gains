package com.digitalcranberry.gainsl.map;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;

import com.digitalcranberry.gainsl.model.events.MapTouchEvent;

import de.greenrobot.event.EventBus;

/**
 * Empty overlay than can be used to detect events on the map,
 * and to throw them to a MapEventsReceiver.
 * @author M.Kergall, modified by Yo Yehudi to use EventBus rather than MapEventReceiver
 */
public class MapEventsOverlay extends Overlay {


    /**
     * @param ctx the context
     * It must implement MapEventsReceiver interface.
     */
    public MapEventsOverlay(Context ctx) {
        super(ctx);
    }

    @Override protected void draw(Canvas c, MapView osmv, boolean shadow) {
        //Nothing to draw
    }

    @Override public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView){
        Projection proj = mapView.getProjection();
        GeoPoint p = (GeoPoint)proj.fromPixels((int)e.getX(), (int)e.getY());
        EventBus.getDefault().post(new MapTouchEvent(p));
        return true;//this could have unexpected side effects. not sure what it does! //TODO
    }

    @Override public boolean onLongPress(MotionEvent e, MapView mapView) {
        Projection proj = mapView.getProjection();
        GeoPoint p = (GeoPoint)proj.fromPixels((int)e.getX(), (int)e.getY());
        //throw event to the receiver:
        EventBus.getDefault().post(new MapTouchEvent(p));
        return true;
    }

}