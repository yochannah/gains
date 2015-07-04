package com.digitalcranberry.gainsl.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.digitalcranberry.gainsl.constants.Constants;
import com.digitalcranberry.gainsl.model.Report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by yo on 14/06/15.

 */
public class ReportCacheManager implements Constants {
    private List<Report> updatedReports;

    public ReportCacheManager() {
    }

    public void save(Context context, Report report, String tableName) {
        CacheDbHelper cacheDbHelper = new CacheDbHelper(context);
        SQLiteDatabase cacher = cacheDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(CacheDbConstants.ReportEntry._ID, UUID.randomUUID().toString());
        values.put(CacheDbConstants.ReportEntry.COL_NAME_CONTENT, report.getContent());
        values.put(CacheDbConstants.ReportEntry.COL_NAME_DATE, report.getDateCreated().toString());

        //let's prevent the nullpointers
        Uri image = report.getImage();
        if(image != null) {
            values.put(CacheDbConstants.ReportEntry.COL_NAME_IMAGEURI, image.toString());
        } else {
            values.put(CacheDbConstants.ReportEntry.COL_NAME_IMAGEURI, "");
        }

        values.put(CacheDbConstants.ReportEntry.COL_NAME_LATITUDE, report.getLatitude());
        values.put(CacheDbConstants.ReportEntry.COL_NAME_LONGITUDE, report.getLongitude());
        values.put(CacheDbConstants.ReportEntry.COL_NAME_STATUS, report.getStatus());

        cacher.insert(
                tableName,
                null,
                values);
        cacher.close();
    }

    public List<Report> getReports(Context context, String tableName){
        List<Report> reports = new ArrayList<>();
        CacheDbHelper cacheDbHelper = new CacheDbHelper(context);
        SQLiteDatabase cacheReader = cacheDbHelper.getReadableDatabase();
        try {
            SimpleDateFormat dateparse = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            String[] projection = {
                    CacheDbConstants.ReportEntry._ID,
                    CacheDbConstants.ReportEntry.COL_NAME_CONTENT,
                    CacheDbConstants.ReportEntry.COL_NAME_DATE,
                    CacheDbConstants.ReportEntry.COL_NAME_STATUS,
                    CacheDbConstants.ReportEntry.COL_NAME_LATITUDE,
                    CacheDbConstants.ReportEntry.COL_NAME_LONGITUDE
            };

            Cursor cursor = cacheReader.query(
                    tableName,  // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                 // The sort order
            );

            while (cursor.moveToNext()) {
                Report rep = new Report(
                        cursor.getString(0), //id
                        cursor.getString(1), //content
                        dateparse.parse(cursor.getString(2)), //date
                        cursor.getString(3), //status
                        cursor.getDouble(4), //lat
                        cursor.getDouble(5), //long
                        Uri.parse("")  //imageURI
                );
                Log.d(DEBUGTAG, rep.getContent());
                reports.add(rep);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cacheReader.close();
        }
        return reports;
    }

    public void moveToSentDb(List<Report> reportsList, Context context){
        Log.i(DEBUGTAG, "Moving " + reportsList.size() + " sent reports from unsent cache to sent db.");
        String selection = CacheDbConstants.UnsentReportEntry._ID + " LIKE ?";
        CacheDbHelper cacheDbHelper = new CacheDbHelper(context);
        SQLiteDatabase cacheKiller = cacheDbHelper.getWritableDatabase();

        for (Report rep : reportsList) {
            String[] selectionArgs = { String.valueOf(rep.getId()) };
            //delete from unsents
            cacheKiller.delete(CacheDbConstants.UnsentReportEntry.TABLE_NAME, selection, selectionArgs);
            //move to sent
            save(context, rep, CacheDbConstants.SentReportEntry.TABLE_NAME);
        }

        reportsList.clear();

        cacheKiller.close();
    }

    public void addServerReports(List<Report> reports){
        updatedReports.addAll(reports);
    }

    public void markAsSent(String reportId) {
        //TODO: Update db to mark a report as sent once it is sent.
    };
}
