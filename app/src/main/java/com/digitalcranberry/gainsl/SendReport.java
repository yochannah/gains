package com.digitalcranberry.gainsl;

import android.util.Log;

import com.digitalcranberry.gainsl.model.Report;
import com.google.gson.Gson;

/**
 * Created by yo on 01/04/15.
 */
public class SendReport {
    //Upload image method

    private static Gson gson = new Gson();

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
