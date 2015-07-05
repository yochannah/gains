package com.digitalcranberry.gainsl.comms;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.digitalcranberry.gainsl.constants.Constants;
import com.digitalcranberry.gainsl.db.CacheDbConstants;
import com.digitalcranberry.gainsl.db.ReportCacheManager;
import com.digitalcranberry.gainsl.map.MapManager;
import com.digitalcranberry.gainsl.model.Report;
import com.digitalcranberry.gainsl.model.events.ReportSent;
import com.digitalcranberry.gainsl.settings.Settings;

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
    private MapManager mapManager;



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
            Log.i(DEBUGTAG, "Sending: " + rep.getContent());
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
        report.setStatus(REPORT_SENT);
        //add map marker
        EventBus.getDefault().post(new ReportSent(report));
        sentReports.add(report);
    }

    /**
     * Checks which reports are new to this phone, and returns only those reports
     * @param remoteReportsList response from remote server
     */
    @Override
    public List<Report> serverReports(List<Report> remoteReportsList) {
        //TODO: Add to list
        List<Report> localReports = getAllReports();
        List<Report> newReports = new ArrayList<>();
        newReports.addAll(remoteReportsList);

        newReports.removeAll(localReports);

        Log.i(DEBUGTAG, "reportslist" + remoteReportsList.toString());
        //ask the db for a reports list we already have.
        return newReports;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(DEBUGTAG,"Activated ReportsCommsService");
        activateReportMonitor();
    }

    private List<Report> getAllReports(){
        List<Report> reports = new ArrayList<>();
        reports.addAll(sentReports);
        reports.addAll(cachedReports);
        return reports;
    }
}
