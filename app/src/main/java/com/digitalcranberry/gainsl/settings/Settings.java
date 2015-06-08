package com.digitalcranberry.gainsl.settings;

/**
 * Created by yo on 07/06/15.
 */
public class Settings {
    public static int getNetworkSendInterval() {
        return networkSendInterval;
    }

    public void setNetworkSendInterval(int networkSendInterval) {
        this.networkSendInterval = networkSendInterval;
    }

    private static int networkSendInterval = 1;

}
