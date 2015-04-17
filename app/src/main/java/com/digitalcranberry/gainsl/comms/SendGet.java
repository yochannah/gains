package com.digitalcranberry.gainsl.comms;

import android.os.AsyncTask;

import com.digitalcranberry.gainsl.model.Report;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by yo on 04/04/15.
 */
public class SendGet extends AsyncTask<URL, Void, String> {

    @Override
    protected String doInBackground(URL... urls) {
        String uploadUrl;

        try {
        URL url = urls[0];
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());
            uploadUrl = response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            uploadUrl = "Problem getting upload URL";
        }
        return uploadUrl;
    }
}
