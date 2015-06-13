package com.digitalcranberry.gainsl.comms;
import static java.util.concurrent.TimeUnit.*;
import static com.digitalcranberry.gainsl.constants.Constants.DEBUGTAG;


import android.os.AsyncTask;
import android.util.Log;

import com.digitalcranberry.gainsl.model.Report;


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

    private static String reportUrl = "http://192.168.1.86:8888/report";
    private Report report;
    private SendReportResult result = null;

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
            { //TODO: Handle errors, e.g. incomplete sends.
                String result = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String output;
                while((output = br.readLine()) != null)
                {
                    result += output;
                }
                System.out.println("Response message: " + result);
            } else {
                Log.w(DEBUGTAG, String.valueOf(responseCode));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        result.updateReportList(report);
    }
}
