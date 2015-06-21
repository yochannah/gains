package com.digitalcranberry.gainsl.comms;
import static java.util.concurrent.TimeUnit.*;
import static com.digitalcranberry.gainsl.constants.Constants.DEBUGTAG;


import android.os.AsyncTask;
import android.util.Log;

import com.digitalcranberry.gainsl.exception.ServerUnavailableException;
import com.digitalcranberry.gainsl.model.Report;
import com.google.gson.Gson;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by yo on 01/04/15.
 */
public class SendReportTask extends AsyncTask<Report, Void, Void> {
    //Upload report method

    private static String reportUrl = "http://gainsl-offline.appspot.com/report";
    private Report report;
    private SendReportResult result = null;
    private boolean success = false;

    public SendReportTask(SendReportResult result) {
        this.result = result;
    }

    @Override
    protected Void doInBackground(Report... reports) {
        report = reports[0];
        String json = report.toQueryParam();
        Log.w(DEBUGTAG,"Sending...");
        try {
            URL url = new URL(reportUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;");
            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes());
            os.flush();
            int responseCode = conn.getResponseCode();

            //get result if there is one
            if(responseCode == 200) //HTTP 200: Response OK
            {
                String result = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String output;
                while((output = br.readLine()) != null)
                {
                    result += output;
                }
                Log.i(DEBUGTAG, "Response message: " + result);
                success = true;
            } else {
                Log.w(DEBUGTAG, String.valueOf(responseCode));
            }
        } catch (Exception e) {
            Log.w(DEBUGTAG, "Unable to connect to server!");
            e.printStackTrace();
            cancel(false);
        }
        return null;
    }

    /**
     * Passes a result back to the Service that initiated this report send.
     * At the current time this results in it being deleted from the cache
     * but only if it was sent successfully if we get a 200 response back)
     * @param aVoid ignore this param
     */
    @Override
    protected void onPostExecute(Void aVoid) {
        if(success) {
            result.updateReportList(report);
        }
    }
}