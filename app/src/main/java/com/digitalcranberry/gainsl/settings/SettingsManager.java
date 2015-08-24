package com.digitalcranberry.gainsl.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.digitalcranberry.gainsl.constants.Constants;

/**
 * Created by yo on 07/06/15.
 */
public class SettingsManager implements Constants {
    public static int getNetworkSendInterval(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String updateFreq = prefs.getString(PREFS_UPDATE_FREQUENCY_MINUTES, "60");
        if(updateFreq.equals("wifi")) {updateFreq = "1";}
        return Integer.parseInt(updateFreq);
    }

    public static String getUpdateConnectionType(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(PREFS_UPDATE_CONNECTION_TYPE, PREFS_UPDATE_CONNECTION_TYPE_DEFAULT);
    }

    public static String getReporter(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(PREFS_UPDATE_REPORTER_NAME, "reporter not set");
    }

}
