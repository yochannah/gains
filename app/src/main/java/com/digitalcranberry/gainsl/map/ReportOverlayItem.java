package com.digitalcranberry.gainsl.map;

import android.util.Log;

import com.digitalcranberry.gainsl.model.Report;
import static com.digitalcranberry.gainsl.constants.Constants.DEBUGTAG;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

/**
 * Created by yo on 05/07/15.
 */
public class ReportOverlayItem extends OverlayItem {
    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    private Report report;
    public ReportOverlayItem(String aUid, String aTitle, String aDescription, GeoPoint aGeoPoint) {
        super(aUid, aTitle, aDescription, aGeoPoint);
    }

    public ReportOverlayItem(Report report) {
        super(report.getId(), "Report", report.toString(), report.getGeopoint());
        this.report = report;
    }


    @Override
    public boolean equals(Object o) {
        return ((ReportOverlayItem) o).getUid().equals(getUid());
    }

    @Override
    public String toString() {
        return this.getUid() + " " + this.getPoint() + " " + this.getSnippet() + " " + this.getTitle();
    }
}
