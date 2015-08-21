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
        int networkSendInterval;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String updateFreq = prefs.getString(PREFS_UPDATE_FREQUENCY_MINUTES, "60");
        if(updateFreq.equals("wifi")) {
            networkSendInterval = 0;
            //TODO: handle Wifi sync.
        } else {
            networkSendInterval = Integer.parseInt(updateFreq);
        }
        return networkSendInterval;
    }

}
