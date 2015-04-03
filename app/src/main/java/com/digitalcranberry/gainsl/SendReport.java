package com.digitalcranberry.gainsl;

import android.os.AsyncTask;
import android.util.Log;

import com.digitalcranberry.gainsl.model.Report;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


import javax.net.ssl.HttpsURLConnection;

/**
 * Created by yo on 01/04/15.
 */
public class SendReport extends AsyncTask<Report, Void, Void> {
    //Upload image method

    private static Gson gson = new Gson();
    private static final String USER_AGENT = "Mozilla/5.0";
    private static String reportUrl = "http://192.168.1.86:8888/report";

    @Override
    protected Void doInBackground(Report... reports) {
        String json = reports[0].toQueryParam();

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
                System.out.println("Response message: " + result);
            }       } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String uploadImage() {
        return ""; //will eventually return the blobstore id
    }
    //upload report method
    public static String uploadReport(Report report) {
        String reportString = gson.toJson(report);
        Log.i("GAINSL",reportString);
        return reportString;
    }
}
