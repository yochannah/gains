package com.digitalcranberry.gainsl.caching;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.digitalcranberry.gainsl.constants.Constants;
import com.digitalcranberry.gainsl.model.Report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
        values.put(CacheDbConstants.ReportEntry._ID, report.getId());
        values.put(CacheDbConstants.ReportEntry.COL_NAME_CONTENT, report.getContent());
        values.put(CacheDbConstants.ReportEntry.COL_NAME_DATE, report.getDateFirstCaptured().toString());

        //let's prevent the nullpointers
        Uri image = report.getImage();
        if(image != null) {
            values.put(CacheDbConstants.ReportEntry.COL_NAME_IMAGEURI, image.toString());
        } else {
            values.put(CacheDbConstants.ReportEntry.COL_NAME_IMAGEURI, "");
        }

        values.put(CacheDbConstants.ReportEntry.COL_NAME_LATITUDE, report.getLatitude());
        values.put(CacheDbConstants.ReportEntry.COL_NAME_LONGITUDE, report.getLongitude());
        values.put(CacheDbConstants.ReportEntry.COL_NAME_SEND_STATUS, report.getSendStatus());
        values.put(CacheDbConstants.ReportEntry.COL_NAME_USER_STATUS, report.getUserStatus());
        values.put(CacheDbConstants.ReportEntry.COL_NAME_DATE_CAPTURED, dateToMs(report.getDateFirstCaptured()));

        //last updated time is always now, because we're updating it again if we're running save.
        values.put(CacheDbConstants.ReportEntry.COL_NAME_LAST_UPDATED, dateToMs(new Date()));

        try {
            cacher.insert(
                    tableName,
                    null,
                    values);
            cacher.close();
        } catch (SQLiteConstraintException e) {
            Log.wtf(DEBUGTAG, "did you see this?!");
            Log.e(DEBUGTAG, "Oh poo.", e);
        }
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
                    CacheDbConstants.ReportEntry.COL_NAME_SEND_STATUS,
                    CacheDbConstants.ReportEntry.COL_NAME_USER_STATUS,
                    CacheDbConstants.ReportEntry.COL_NAME_LATITUDE,
                    CacheDbConstants.ReportEntry.COL_NAME_LONGITUDE,
                    CacheDbConstants.ReportEntry.COL_NAME_DATE_CAPTURED,
                    CacheDbConstants.ReportEntry.COL_NAME_LAST_UPDATED
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
                        cursor.getString(3), //sendstatus
                        cursor.getString(4), //userstatus
                        cursor.getDouble(5), //lat
                        cursor.getDouble(6), //long
                        Uri.parse(""),       //imageURI
                        new Date(Long.parseLong(cursor.getString(8)))    //dateCaptured
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

    public long getNumOfReports(String tableName, Context context){
        long reportNum = 0;

        CacheDbHelper cacheDbHelper = new CacheDbHelper(context);
        SQLiteDatabase cacheReader = cacheDbHelper.getReadableDatabase();
        reportNum = DatabaseUtils.queryNumEntries(cacheReader,tableName);
        cacheReader.close();

        return reportNum;
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
            saveOrUpdate(CacheDbConstants.SentReportEntry.TABLE_NAME, context, rep);
        }

        reportsList.clear();
        cacheKiller.close();
    }

    private void update(Context context, Report report, String tableName) {
        Log.i(DEBUGTAG, "executing update for" + report.describeContents());
        CacheDbHelper cacheDbHelper = new CacheDbHelper(context);
        SQLiteDatabase cacher = cacheDbHelper.getWritableDatabase();
        String whereClause = CacheDbConstants.ReportEntry._ID  + " LIKE ?";
        String[] selectionArgs = { String.valueOf(report.getId()) };

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(CacheDbConstants.ReportEntry.COL_NAME_CONTENT, report.getContent());
        values.put(CacheDbConstants.ReportEntry.COL_NAME_LAST_UPDATED, dateToMs(report.getLastUpdated()));
        values.put(CacheDbConstants.ReportEntry.COL_NAME_USER_STATUS, report.getUserStatus());

        cacher.update(tableName, values, whereClause, selectionArgs);
        cacher.close();
    }

    public void addSentReports(List<Report> reports, Context context){
        for (Report rep : reports) {
            saveOrUpdate(CacheDbConstants.SentReportEntry.TABLE_NAME,context, rep);
        }
    }

    public boolean doesReportExist(String tableName, Context context, String reportid){
        boolean reportExists = false;

        CacheDbHelper cacheDbHelper = new CacheDbHelper(context);
        SQLiteDatabase cacheReader = cacheDbHelper.getReadableDatabase();

        String queryString = CacheDbConstants.ReportEntry._ID + " LIKE ?";
        String[] selectionArgs = {reportid};

        reportExists = (DatabaseUtils.queryNumEntries(cacheReader,tableName, queryString, selectionArgs) > 0);
        cacheReader.close();

        return reportExists;
    }

    public void saveOrUpdate(String tableName, Context context, Report report) {
        if(doesReportExist(tableName, context, report.getId())) {
            Log.w(DEBUGTAG, "=============++++++========UPDATING"+ report.getId());
            update(context, report,tableName);
        } else {
            save(context, report, tableName);
        }
    }

    private long dateToMs(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return  c.getTimeInMillis();
    }


}
