package com.digitalcranberry.gainsl.comms;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.digitalcranberry.gainsl.constants.Constants;
import com.digitalcranberry.gainsl.caching.CacheDbConstants;
import com.digitalcranberry.gainsl.caching.ReportCacheManager;
import com.digitalcranberry.gainsl.model.Report;
import com.digitalcranberry.gainsl.model.events.report.Sent;
import com.digitalcranberry.gainsl.model.events.report.Updated;
import com.digitalcranberry.gainsl.model.events.report.ServerReportsReceived;
import com.digitalcranberry.gainsl.settings.SettingsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import de.greenrobot.event.EventBus;

import static com.digitalcranberry.gainsl.constants.ReportStatuses.REPORT_SENT;
import static java.util.concurrent.TimeUnit.MINUTES;


/**
 * Created by yo on 05/06/15.
 */
public class ReportCommsService extends IntentService implements Constants, SendReportResult {

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);
    private List<Report> sentReports;
    private ReportCacheManager cacheManager;
    private List<Report> cachedReports;




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
        int interval = SettingsManager.getNetworkSendInterval(this);
        Log.i(DEBUGTAG, "Starting report sync service with interval of " + interval + " minutes");
        final Context context = this;

        final Runnable saver = new Runnable() {

            public void run() {
                //clear out successfully sent reports.
                cacheManager.moveToSentDb(sentReports, context);

                //send any new ones, if we can see the intertubes.
                cachedReports = cacheManager.getReports(context, CacheDbConstants.UnsentReportEntry.TABLE_NAME);

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

    public List<Report> sendReport(Report report) {
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
        //let's return new reports received from the server.
        return null;
    }

    private List<Report> sendAllReports(List<Report> reports) {
        Log.i(DEBUGTAG, "Sending " + reports.size() + " stored reports");

        for (Report rep : reports) {
            Log.i(DEBUGTAG, "Sending: " + rep.toString() + " " + rep.toQueryParam());
            sendReport(rep);      // sends the report.
        }
        return null; //TODO, correct return handling
    }


    /**
     * Callback from the asynctask that sendsreports, adding
     * the attached report argument to a list to be deleted
     * @param report The report that has been successfully sent
     */
    @Override
    public void updateReportList(Report report) {
        report.setSendStatus(REPORT_SENT);
        //add map marker
        List<Report> reports = new ArrayList<>();
        reports.add(report);
        EventBus.getDefault().post(new Sent(reports));
    }

    /**
     * Checks which reports are new to this phone, and returns only those reports
     * @param remoteReportsList response from remote server
     */
    @Override
    public void serverReports(List<Report> remoteReportsList) {
        //TODO: Add to list
        List<Report> localReports = getAllReports();
        List<Report> newReports = new ArrayList<>();
        List<Report> changedReports = new ArrayList<>();
        List<Report> existingReports = new ArrayList<>();

        //populate the two lists with the reports
        newReports.addAll(remoteReportsList);
        existingReports.addAll(remoteReportsList);

        //add all existing reports to existing list, and only keep changed ones.
        for (Report report : existingReports) {
            //get the matching report in temp
            int location = localReports.indexOf(report);
            if((location >=0) && localReports.get(location).hasChanged(report)) {
                changedReports.add(report);
            }
        }

        //purge existing reports from the new list
        newReports.removeAll(localReports);

        if(remoteReportsList.size() > 0) {
            Log.i(DEBUGTAG, remoteReportsList.size() + " server reports received.");
            EventBus.getDefault().post(new ServerReportsReceived(newReports));
        }
        if (changedReports.size() > 0) {
            Log.i(DEBUGTAG,remoteReportsList.size() + " server reports updated.");
            EventBus.getDefault().post(new Updated(changedReports));
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(DEBUGTAG,"Activated ReportsCommsService");
        activateReportMonitor();
    }

    private List<Report> getAllReports(){
        List<Report> reports = new ArrayList<>();
        reports.addAll(cacheManager.getReports(this, CacheDbConstants.SentReportEntry.TABLE_NAME));
        reports.addAll(cachedReports);
        return reports;
    }
}
