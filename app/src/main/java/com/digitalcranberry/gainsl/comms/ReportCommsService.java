package com.digitalcranberry.gainsl.comms;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.digitalcranberry.gainsl.constants.Constants;
import com.digitalcranberry.gainsl.db.ReportCacheManager;
import com.digitalcranberry.gainsl.model.Report;
import com.digitalcranberry.gainsl.settings.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static com.digitalcranberry.gainsl.constants.ReportStatuses.REPORT_SENT;
import static java.util.concurrent.TimeUnit.MINUTES;


/**
 * Created by yo on 05/06/15.
 */
public class ReportCommsService extends IntentService implements Constants, SendReportResult{

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);
    private List<Report> sentReports;
    ReportCacheManager cacheManager;


    public ReportCommsService(){
        super("ReportCommsService");
        this.sentReports = new ArrayList<>();
        this.cacheManager = new ReportCacheManager();
    }


    /**
     * Sends the report every interval based on user preferences.
     * This method periodically sends anything that has been stored previously,
     * and removes successfully sent items.
     **/
    public void activateReportMonitor() {
        int interval = Settings.getNetworkSendInterval();
        final Context context = this;

        final Runnable saver = new Runnable() {
            List<Report> cachedReports;

            public void run() {
                //clear out successfully sent reports.
                cacheManager.deleteReportList(sentReports, context);

                //send any new ones, if we can see the intertubes.
                cachedReports = cacheManager.getCachedReports(context);
                if (NetworkStatus.isConnected(context)) {
                    sendAllReports(cachedReports);
                } else {
                    Log.i(DEBUGTAG, "Network not present, not sending yet");
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

    private void sendAllReports(List<Report> reports) {
        Log.i(DEBUGTAG, "Sending " + reports.size() + " stored reports");

        for (Report rep : reports) {
            Log.i(DEBUGTAG, "Sending: " + rep.getContent());
            sendReport(rep);      // sends the report.
        }
    }


    /**
     * Callback from the asynctask that sendsreports, adding
     * the attached report argument to a list to be deleted
     * @param report The report that has been successfully sent
     */
    @Override
    public void updateReportList(Report report) {
        report.setStatus(REPORT_SENT);
        sentReports.add(report);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(DEBUGTAG,"Activated ReportsCommsService");
        activateReportMonitor();
    }
}
