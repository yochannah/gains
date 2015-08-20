package com.digitalcranberry.gainsl.map;

import com.digitalcranberry.gainsl.model.Report;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

/**
 * Created by yo on 20/08/15.
 */
public class OverlayItemWithReport extends OverlayItem {
    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    private Report report;
    public OverlayItemWithReport(String aTitle, String aSnippet, IGeoPoint aGeoPoint, Report report) {
        super(aTitle, aSnippet, aGeoPoint);
        this.report = report;
    }

    public OverlayItemWithReport(String aUid, String aTitle, String aDescription, IGeoPoint aGeoPoint, Report report) {
        super(aUid, aTitle, aDescription, aGeoPoint);
        this.report = report;
    }
}
