package com.digitalcranberry.gainsl.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.digitalcranberry.gainsl.comms.SendReport;
import com.digitalcranberry.gainsl.comms.UploadImage;
import com.digitalcranberry.gainsl.model.Report;
import com.digitalcranberry.gainsl.cache.CacheDbConstants.ReportEntry;


/**
 * Created by yo on 05/06/15.
 */
public class SaveReport {

    private final Context context;
    private final Report report;

    public SaveReport(Report report, Context context) {
        this.report = report;
        this.context = context;
    }

    public void save() {

        CacheDbHelper cacheDbHelper = new CacheDbHelper(context);
        SQLiteDatabase cacher = cacheDbHelper.getWritableDatabase();


        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ReportEntry.COL_NAME_CONTENT, report.getContent());
        values.put(ReportEntry.COL_NAME_DATE, report.getDateCreated().toString());
        values.put(ReportEntry.COL_NAME_IMAGEURI, report.getImage().toString());
        values.put(ReportEntry.COL_NAME_LATITUDE, report.getLatitude());
        values.put(ReportEntry.COL_NAME_LONGITUDE, report.getLongitude());
        values.put(ReportEntry.COL_NAME_STATUS, report.getStatus());

        long newRowId;
        newRowId = cacher.insert(
                ReportEntry.TABLE_NAME,
                null,
                values);
    }

    public void send() {

        try {
            if (report.getImage() != null) {
                UploadImage ui = new UploadImage();
                String url = ui.getUploadURL();
                ui.upload(url, report.getImage());
            }

            new SendReport().execute(report);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
