package com.digitalcranberry.gainsl.map;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.digitalcranberry.gainsl.R;
import com.digitalcranberry.gainsl.constants.Constants;
import com.digitalcranberry.gainsl.constants.ReportStatuses;
import com.digitalcranberry.gainsl.db.CacheDbConstants;
import com.digitalcranberry.gainsl.db.ReportCacheManager;
import com.digitalcranberry.gainsl.model.Report;
import com.digitalcranberry.gainsl.model.events.ReportCreated;
import com.digitalcranberry.gainsl.model.events.ReportSent;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.CloudmadeUtil;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by yo on 17/04/15.
 */
public class MapFragment extends Fragment implements Constants {
    private ResourceProxyImpl mResourceProxy;
    private MapView mMapView;
    private SharedPreferences mPrefs;

    private CompassOverlay mCompassOverlay;
    private MyLocationNewOverlay mLocationOverlay;
    private ScaleBarOverlay mScaleBarOverlay;
    private static int MENU_LAST_ID = 1;
    private ArrayList<OverlayItem> mOverlayItems = new ArrayList<OverlayItem>();
    private ItemizedIconOverlay mMarkerOverlay;
    private ItemizedIconOverlay.OnItemGestureListener<OverlayItem> mOnItemGestureListener;


    private Map<String, Integer> markerDrawables;
    private MapManager mapManager;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(ReportCreated event){

        Toast.makeText(getActivity(), R.string.report_captured + " " + event.report.toString(), Toast.LENGTH_SHORT).show();
        addMapMarker(event.report);
    }

    public void onEvent(ReportSent event){
        updateMapMarker(event.report);
    }

    //to be refactored to preference UI later
    private void setPrefs() {
        Context context = getActivity();
        mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(PREFS_ZOOM_LEVEL, 7);
        editor.putInt(PREFS_SCROLL_Y, -1);
        editor.putInt(PREFS_SCROLL_X, 53);
        editor.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mResourceProxy = new ResourceProxyImpl(inflater.getContext().getApplicationContext());
        setPrefs();
        mMapView = new MapView(inflater.getContext(), 256, mResourceProxy);

        return mMapView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        markerDrawables = new HashMap<>();
        markerDrawables.put(ReportStatuses.REPORT_SENT,  R.drawable.ic_action_place_orange);
        markerDrawables.put(ReportStatuses.REPORT_UNSENT,  R.drawable.ic_action_place_blue);

        final Context context = this.getActivity();
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();

        mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // only do static initialisation if needed
        if (CloudmadeUtil.getCloudmadeKey().length() == 0) {
            CloudmadeUtil.retrieveCloudmadeKey(context.getApplicationContext());
        }

        this.mCompassOverlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context),
                mMapView);
        this.mLocationOverlay = new MyLocationNewOverlay(context, new GpsMyLocationProvider(context),
                mMapView);

        prepareReportMarkers();
        this.mMarkerOverlay = new ItemizedIconOverlay<OverlayItem>(mOverlayItems, mOnItemGestureListener, mResourceProxy);

        mScaleBarOverlay = new ScaleBarOverlay(context);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);


        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        mMapView.getOverlays().add(this.mLocationOverlay);
        mMapView.getOverlays().add(this.mCompassOverlay);
        mMapView.getOverlays().add(this.mScaleBarOverlay);
        mMapView.getOverlays().add(this.mMarkerOverlay);

        mMapView.getController().setZoom(mPrefs.getInt(PREFS_ZOOM_LEVEL, 1));
        //mMapView.scrollTo(mPrefs.getInt(PREFS_SCROLL_X, 0), mPrefs.getInt(PREFS_SCROLL_Y, 0));


        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        mCompassOverlay.enableCompass();

        setHasOptionsMenu(true);
    }

    @Override
    public void onPause()
    {
        final SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString(PREFS_TILE_SOURCE, mMapView.getTileProvider().getTileSource().name());
        edit.putInt(PREFS_SCROLL_X, mMapView.getScrollX());
        edit.putInt(PREFS_SCROLL_Y, mMapView.getScrollY());
        edit.putInt(PREFS_ZOOM_LEVEL, mMapView.getZoomLevel());
        edit.putBoolean(PREFS_SHOW_LOCATION, mLocationOverlay.isMyLocationEnabled());
        edit.putBoolean(PREFS_SHOW_COMPASS, mCompassOverlay.isCompassEnabled());
        edit.commit();

        this.mLocationOverlay.disableMyLocation();
        this.mCompassOverlay.disableCompass();

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
        if (mPrefs.getBoolean(PREFS_SHOW_LOCATION, false)) {
            this.mLocationOverlay.enableMyLocation();
        }
        if (mPrefs.getBoolean(PREFS_SHOW_COMPASS, false)) {
            this.mCompassOverlay.enableCompass();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Put overlay items first
        mMapView.getOverlayManager().onCreateOptionsMenu(menu, MENU_LAST_ID, mMapView);

        super.onCreateOptionsMenu(menu, inflater);
    }

    private OverlayItem generateReportMarker(Report report, int drawable) {
        GeoPoint point = new GeoPoint(report.getLatitude(), report.getLongitude());
        OverlayItem olItem = new OverlayItem("Report", report.getContent(), point);
        Drawable newMarker = this.getResources().getDrawable(drawable);
        olItem.setMarker(newMarker);
        return olItem;
    }


    private void prepareReportMarkers() {
        //get saved report markers
        ReportCacheManager cm = new ReportCacheManager();
        List<Report> sentReports = cm.getReports(getActivity(), CacheDbConstants.SentReportEntry.TABLE_NAME);
        List<Report> unsentReports = cm.getReports(getActivity(), CacheDbConstants.UnsentReportEntry.TABLE_NAME);
        for (Report report : sentReports) {
            addMapMarker(report);
        }
        for (Report report : unsentReports) {
            addMapMarker(report);
        }

        mOnItemGestureListener
                = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>(){

            @Override
            public boolean onItemLongPress(int arg0, OverlayItem arg1) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onItemSingleTapUp(int index, OverlayItem item) {
                Toast.makeText(getActivity(),item.getSnippet(),
                        Toast.LENGTH_LONG).show();

                return true;
            }

        };
    }

    public void addMapMarker(Report report) {
        try {
            OverlayItem marker = generateReportMarker(report, markerDrawables.get(report.getStatus()));
            mOverlayItems.add(marker);
            mMarkerOverlay.addItem(marker);
            mMapView.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeMapMarker(Report report){
        OverlayItem marker = generateReportMarker(report, markerDrawables.get(report.getStatus()));
        mMarkerOverlay.removeItem(marker);
        mMapView.invalidate();
    }

    public void updateMapMarker(Report report) {
        Log.i(DEBUGTAG,"report update received!");
        removeMapMarker(report);
        addMapMarker(report);
        mMapView.invalidate();
    }
}
