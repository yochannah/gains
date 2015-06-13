package com.digitalcranberry.gainsl.comms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.digitalcranberry.gainsl.constants.Constants;
import static com.digitalcranberry.gainsl.constants.ReportStatuses.*;
import com.digitalcranberry.gainsl.model.Report;
import com.digitalcranberry.gainsl.comms.CacheDbConstants.ReportEntry;
import com.digitalcranberry.gainsl.settings.Settings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.MINUTES;


/**
 * Created by yo on 05/06/15.
 */
public class ReportManager implements Constants, SendReportResult {

    private final Context context;
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);
    private CacheDbHelper cacheDbHelper;// = new CacheDbHelper(context);
    private List<Report> sentReports;

    public ReportManager(Context context) {
        this.context = context;
        this.sentReports = new ArrayList<>();
        cacheDbHelper = new CacheDbHelper(context);
    }

    public void save(Report report) {
        SQLiteDatabase cacher = cacheDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ReportEntry.COL_NAME_CONTENT, report.getContent());
        values.put(ReportEntry.COL_NAME_DATE, report.getDateCreated().toString());

        //let's prevent the nullpointers
        Uri image = report.getImage();
        if(image != null) {
            values.put(ReportEntry.COL_NAME_IMAGEURI, image.toString());
        } else {
            values.put(ReportEntry.COL_NAME_IMAGEURI, "");
        }

        values.put(ReportEntry.COL_NAME_LATITUDE, report.getLatitude());
        values.put(ReportEntry.COL_NAME_LONGITUDE, report.getLongitude());
        values.put(ReportEntry.COL_NAME_STATUS, report.getStatus());

        cacher.insert(
                ReportEntry.TABLE_NAME,
                null,
                values);
        activateReportMonitor();
    }

    public List<Report> getCachedReports(){
        List<Report> reports = new ArrayList<>();
        try {
            SimpleDateFormat dateparse = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            SQLiteDatabase cacheReader = cacheDbHelper.getReadableDatabase();
            String[] projection = {
                    ReportEntry._ID,
                    ReportEntry.COL_NAME_CONTENT,
                    ReportEntry.COL_NAME_DATE,
                    ReportEntry.COL_NAME_STATUS,
                    ReportEntry.COL_NAME_LATITUDE,
                    ReportEntry.COL_NAME_LONGITUDE
            };

            Cursor cursor = cacheReader.query(
                    ReportEntry.TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                 // The sort order
            );

            while (cursor.moveToNext()) {
                Report rep = new Report(
                        cursor.getInt(0), //id
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
        }catch (Exception e) {
            e.printStackTrace();
        }
        return reports;
    }

    /**
     * Sends the report every interval based on user preferences.
     * This method periodically sends anything that has been stored previously,
     * and removes successfully sent items.
     **/
    public void activateReportMonitor() {
        int interval = Settings.getNetworkSendInterval();

        final Runnable saver = new Runnable() {
            List<Report> cachedReports;

            public void run() {
                //clear out successfully sent reports.
                deleteReportList(sentReports);

                //send any new ones.
                cachedReports = getCachedReports();
                Log.i(DEBUGTAG, "Sending " + cachedReports.size() + " stored reports");

                for (Report rep : cachedReports) {
                    Log.i(DEBUGTAG, "Sending: " + rep.getContent());
                    sendReport(rep);      // sends the report.
                }
            }
        };

        final ScheduledFuture saverHandle =
                scheduler.scheduleAtFixedRate(saver, interval, interval, MINUTES);
        scheduler.schedule(new Runnable() {
                public void run () {saverHandle.cancel(true);}
        },60*60,MINUTES);
    }

    public void sendReport(Report report) {
        try {


            new SendReportTask(this).execute(report);

            //todo: fix image uploads
           /* if (report.getImage() != null) {
                UploadImage ui = new UploadImage();
                String url = ui.getUploadURL();
                ui.upload(url, report.getImage());
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteReportList(List<Report> reportsList){
        Log.i(DEBUGTAG,"Deleting " + reportsList.size() + " sent reports from cache");
        String selection = ReportEntry._ID + " LIKE ?";
        cacheDbHelper = new CacheDbHelper(context);
        SQLiteDatabase cacheKiller = cacheDbHelper.getWritableDatabase();
        for (Report rep : reportsList) {
            String[] selectionArgs = { String.valueOf(rep.getId()) };
            cacheKiller.delete(ReportEntry.TABLE_NAME, selection, selectionArgs);
        }
        reportsList.clear();
    }

    @Override
    public void updateReportList(Report report) {
        report.setStatus(REPORT_SENT);
        sentReports.add(report);
    }
}
