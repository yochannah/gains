package com.digitalcranberry.gainsl.map;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.CloudmadeUtil;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.digitalcranberry.gainsl.NewReportDialog;
import com.digitalcranberry.gainsl.R;
import com.digitalcranberry.gainsl.constants.Constants;
import com.digitalcranberry.gainsl.db.CacheDbConstants;
import com.digitalcranberry.gainsl.db.ReportCacheManager;
import com.digitalcranberry.gainsl.model.Report;

import java.util.ArrayList;
import java.util.List;

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

    //to be refactored to preference UI later
    private void setPrefs() {
        Context context = getActivity();
        mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(PREFS_ZOOM_LEVEL, 10);
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
        List<Report> sentReports = cm.getReports(getActivity(), CacheDbConstants.UnsentReportEntry.TABLE_NAME);
        List<Report> unsentReports = cm.getReports(getActivity(), CacheDbConstants.SentReportEntry.TABLE_NAME);
        for (Report report : sentReports) {
            mOverlayItems.add(generateReportMarker(report, (R.drawable.ic_action_place_blue)));
        }
        for (Report report : unsentReports) {
            mOverlayItems.add(generateReportMarker(report, (R.drawable.ic_action_place_orange)));
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

}
