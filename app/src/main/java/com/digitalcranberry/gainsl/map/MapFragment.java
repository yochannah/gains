package com.digitalcranberry.gainsl.map;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.digitalcranberry.gainsl.R;
import com.digitalcranberry.gainsl.caching.PendingReportCounter;
import com.digitalcranberry.gainsl.caching.TileCacheManager;
import com.digitalcranberry.gainsl.constants.Constants;
import com.digitalcranberry.gainsl.constants.ReportStatuses;
import com.digitalcranberry.gainsl.caching.CacheDbConstants;
import com.digitalcranberry.gainsl.caching.ReportCacheManager;
import com.digitalcranberry.gainsl.dialog.NewReportDialog;
import com.digitalcranberry.gainsl.dialog.ReportDetailsDialog;
import com.digitalcranberry.gainsl.model.Report;
import com.digitalcranberry.gainsl.model.events.map.AddOverlay;
import com.digitalcranberry.gainsl.model.events.map.RemoveOverlay;
import com.digitalcranberry.gainsl.model.events.report.Created;
import com.digitalcranberry.gainsl.model.events.report.Sent;
import com.digitalcranberry.gainsl.model.events.report.ServerReportsReceived;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.CloudmadeUtil;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
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
    private MyMarkerOverlay mMarkerOverlay;
    private ItemizedIconOverlay.OnItemGestureListener<OverlayItem> mOnItemGestureListener;


    private Map<String, Integer> markerDrawables;

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
    Eventbus event handler for newreport creation. Adds map marker.
     */
    public void onEvent(Created event){
        Toast.makeText(getActivity(), R.string.report_captured + " " + event.report.toString(), Toast.LENGTH_SHORT).show();
        addMapMarker(event.report);
    }

    /*
    Eventbus event handler for changing map marker to 'sent' colour when sent
     */
    public void onEvent(Sent event){

        Context context = this.getActivity();
        ReportCacheManager cacheManager = new ReportCacheManager();
        for (Report report : event.reports) {
            updateMapMarker(report);
        }
        cacheManager.moveToSentDb(event.reports, context);
        PendingReportCounter.updatePendingReportCount(context);
        }

    /*
    Eventbus event handler for en-masse adding of server-side reports
     */
    public void onEvent(ServerReportsReceived event){
        ReportCacheManager cacheManager = new ReportCacheManager();
        for (Report report : event.reports) {
            addMapMarker(report);
        }
        cacheManager.addSentReports(event.reports, this.getActivity());
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

    //to be refactored to preference UI later. Sane defaults set for now.
    private void setPrefs() {
        Context context = getActivity();
        mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(PREFS_ZOOM_LEVEL, 7);
        editor.putInt(PREFS_SCROLL_Y, -1);
        editor.putInt(PREFS_SCROLL_X, 53);
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

        //test bb.
        BoundingBoxE6 bb = new BoundingBoxE6(-43.423, 172.728, -43.611, 172.455);
        cacheTiles(bb);

        return mMapView;
    }

    /**
     * Adds markers and various overlay layers to maps.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeMarkerDrawables();
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

        this.mCompassOverlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context),
                mMapView);
        this.mLocationOverlay = new MyLocationNewOverlay(context, new GpsMyLocationProvider(context),
                mMapView);

        prepareReportMarkers();
        this.mMarkerOverlay = new MyMarkerOverlay<OverlayItem>(mOverlayItems, mOnItemGestureListener, mResourceProxy);

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

    private int getStatusMarker(String status) {
        try {
            return markerDrawables.get(status);
        } catch (NullPointerException e) {
            return markerDrawables.get(ReportStatuses.REPORT_NEW);
        }
    }

    public void initializeMarkerDrawables() {
        markerDrawables = new HashMap<>();
        markerDrawables.put(ReportStatuses.REPORT_SENT, R.drawable.ic_action_place_orange);
        markerDrawables.put(ReportStatuses.REPORT_NEW, R.drawable.ic_action_place_orange);
        markerDrawables.put(ReportStatuses.REPORT_UNSENT, R.drawable.ic_action_place_blue);
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

    private OverlayItem generateReportMarker(Report report) {
        int drawable = getStatusMarker(report.getStatus());
        GeoPoint point = new GeoPoint(report.getLatitude(), report.getLongitude());
        OverlayItem olItem = new ReportOverlayItem(report);
        Drawable newMarker = this.getResources().getDrawable(drawable);
        olItem.setMarker(newMarker);
        return olItem;
    }

    public void addOverlay(Overlay overlay){
        mMapView.getOverlays().add(overlay);
    }

    public void removeOverlay(Overlay overlay){
        mMapView.getOverlays().remove(overlay);
    }

    private void prepareReportMarkers() {
        //get saved report markers
        ReportCacheManager cm = new ReportCacheManager();
        List<Report> reports = cm.getReports(getActivity(), CacheDbConstants.SentReportEntry.TABLE_NAME);
        reports.addAll(cm.getReports(getActivity(), CacheDbConstants.UnsentReportEntry.TABLE_NAME));
        for (Report report : reports) {
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
                ReportDetailsDialog d = new ReportDetailsDialog();
                d.setSnippet(item.getSnippet());
                d.show(getFragmentManager(), "ReportDetailsDialog");

//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
//                if (prev != null) {
//                    ft.remove(prev);
//                }
//                ft.addToBackStack(null);
//
//                DialogFragment newFragment = ReportDetailsDialog.newInstance(item.getSnippet());
//                newFragment.show("dialog");

                return true;
            }

        };
    }

    public void addMapMarker(Report report) {
        try {
            OverlayItem marker = generateReportMarker(report);
            mOverlayItems.add(marker);
            mMarkerOverlay.addItem(marker);
            mMapView.invalidate();
            Log.i(DEBUGTAG, "Adding " + report.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeMapMarker(Report report){
        OverlayItem marker = generateReportMarker(report);
        if(mMarkerOverlay.contains(marker)) {
            Log.i(DEBUGTAG,"Removing " + report.toString());
            mMarkerOverlay.removeItem(marker);
        }
        mMapView.invalidate();
    }

    public void updateMapMarker(Report report) {
        Log.i(DEBUGTAG,"Updating " + report.toString());
        removeMapMarker(report);
        addMapMarker(report);
        mMapView.invalidate();
    }

    private void cacheTiles(BoundingBoxE6 bb) {
        TileCacheManager tcm = new TileCacheManager(mMapView);
        tcm.downloadAreaAsync(this.getActivity(),bb,10,10);
        Log.i(DEBUGTAG,"caching!!");
    }
}
