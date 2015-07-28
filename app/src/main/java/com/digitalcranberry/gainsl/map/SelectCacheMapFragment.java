package com.digitalcranberry.gainsl.map;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digitalcranberry.gainsl.caching.TileCacheManager;
import com.digitalcranberry.gainsl.constants.Constants;
import com.digitalcranberry.gainsl.model.events.map.AddOverlay;
import com.digitalcranberry.gainsl.model.events.map.RemoveOverlay;
import com.digitalcranberry.gainsl.model.events.map.TouchEvent;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.CloudmadeUtil;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by yo on 17/04/15.
 */
public class SelectCacheMapFragment extends Fragment implements Constants {
    private ResourceProxyImpl mResourceProxy;
    private MapView mMapView;
    private MapEventsOverlay mMapEventOverlay;
    private MyLocationNewOverlay mLocationOverlay;

    private SharedPreferences mPrefs;

    private ScaleBarOverlay mScaleBarOverlay;
    private static int MENU_LAST_ID = 1;
    private ArrayList<OverlayItem> mOverlayItems = new ArrayList<OverlayItem>();

    /*
    Binds Eventbus listener
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    /*
    Unbinds Eventbus listener
     */
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /*
Eventbus event handler for adding and removing overlays
 */
    public void onEvent(AddOverlay event){
        addOverlay(event.overlay);
        mMapView.invalidate();
    }
    public void onEvent(RemoveOverlay event){
        removeOverlay(event.overlay);
        mMapView.invalidate();
    }
    public void onEvent(TouchEvent event){
        Log.i(DEBUGTAG,event.point.toString());
    }

    //to be refactored to preference UI later. Sane defaults set for now.
    private void setPrefs() {
        Context context = getActivity();
        mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(PREFS_ZOOM_LEVEL, 7);
        editor.putInt(PREFS_SCROLL_Y, 53);
        editor.putInt(PREFS_SCROLL_X, -1);
        editor.putInt(PREFS_DEFAULT_SCROLL_Y, 53);
        editor.putInt(PREFS_DEFAULT_SCROLL_X, -1);

        editor.commit();
    }

    /*
    * Initialize map and initiate tile caching if there is internet.
    * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mResourceProxy = new ResourceProxyImpl(inflater.getContext().getApplicationContext());
        setPrefs();
        mMapView = new MapView(inflater.getContext(), 256, mResourceProxy);

        mMapView.getController().setZoom(mPrefs.getInt(PREFS_ZOOM_LEVEL,10));

        //test bb.
       // BoundingBoxE6 bb = new BoundingBoxE6(-43.423, 172.728, -43.611, 172.455);
       // cacheTiles(bb);

        return mMapView;
    }

    /**
     * Adds markers and various overlay layers to maps.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeOverlaysAndSettings();
    }

    private void initializeOverlaysAndSettings() {

        final Context context = this.getActivity();
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();

        mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // only do static initialisation if needed
        if (CloudmadeUtil.getCloudmadeKey().length() == 0) {
            CloudmadeUtil.retrieveCloudmadeKey(context.getApplicationContext());
        }

        mMapEventOverlay = new MapEventsOverlay(context);


        mScaleBarOverlay = new ScaleBarOverlay(context);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);

        this.mLocationOverlay = new MyLocationNewOverlay(context, new GpsMyLocationProvider(context),
                mMapView);



        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);

        mMapView.getOverlays().add(this.mScaleBarOverlay);
        mMapView.getOverlays().add(this.mMapEventOverlay);
        mMapView.getOverlays().add(this.mLocationOverlay);

        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();

        mMapView.getController().setZoom(mPrefs.getInt(PREFS_ZOOM_LEVEL, 1));
        //todo: remove UK hardcode
       // mMapView.scrollTo(mPrefs.getInt(PREFS_SCROLL_X, -1),mPrefs.getInt(PREFS_SCROLL_X,53));
        mMapView.getController().setCenter(new GeoPoint(mPrefs.getInt(PREFS_DEFAULT_SCROLL_X, -1),mPrefs.getInt(PREFS_DEFAULT_SCROLL_Y,53)));
    }

    @Override
    public void onPause()
    {
        final SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString(PREFS_TILE_SOURCE, mMapView.getTileProvider().getTileSource().name());
        edit.putInt(PREFS_SCROLL_X, mMapView.getScrollX());
        edit.putInt(PREFS_SCROLL_Y, mMapView.getScrollY());
        edit.putInt(PREFS_ZOOM_LEVEL, mMapView.getZoomLevel());
        edit.commit();


        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        final String tileSourceName = mPrefs.getString(PREFS_TILE_SOURCE,
                TileSourceFactory.DEFAULT_TILE_SOURCE.name());
        try {
            final ITileSource tileSource = TileSourceFactory.getTileSource(tileSourceName);
            mMapView.setTileSource(tileSource);
        } catch (final IllegalArgumentException ignore) {
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Put overlay items first
        mMapView.getOverlayManager().onCreateOptionsMenu(menu, MENU_LAST_ID, mMapView);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void addOverlay(Overlay overlay){
        mMapView.getOverlays().add(overlay);
    }

    public void removeOverlay(Overlay overlay){
        mMapView.getOverlays().remove(overlay);
    }

    private void cacheTiles(BoundingBoxE6 bb) {
        TileCacheManager tcm = new TileCacheManager(mMapView);
        tcm.downloadAreaAsync(this.getActivity(),bb,10,10);
        Log.i(DEBUGTAG,"caching!!");
    }
}
