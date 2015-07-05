package com.digitalcranberry.gainsl.map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.osmdroid.ResourceProxy;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.List;

/**
 * Created by yo on 05/07/15.
 */
public class MyMarkerOverlay<Item extends OverlayItem> extends ItemizedIconOverlay {
    public MyMarkerOverlay(List pList, Drawable pDefaultMarker, OnItemGestureListener pOnItemGestureListener, ResourceProxy pResourceProxy) {
        super(pList, pDefaultMarker, pOnItemGestureListener, pResourceProxy);
    }

    public MyMarkerOverlay(List pList, OnItemGestureListener pOnItemGestureListener, ResourceProxy pResourceProxy) {
        super(pList, pOnItemGestureListener, pResourceProxy);
    }

    public MyMarkerOverlay(Context pContext, List pList, OnItemGestureListener pOnItemGestureListener) {
        super(pContext, pList, pOnItemGestureListener);
    }

    public boolean contains(OverlayItem oi) {
        return this.mItemList.contains(oi);
    }

    public OverlayItem get(OverlayItem oi) {
        int toGet = this.mItemList.indexOf(oi);
        return (OverlayItem) this.mItemList.get(toGet);
    }

    public void booya() {
        Log.i("BOOYA", "booya");
    }
}
