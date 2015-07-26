package com.digitalcranberry.gainsl.model.events;

import android.util.Log;

/**
 * Created by yo on 07/07/15.
 */
public class PendingReportCountUpdated {
    public long reports;
    public PendingReportCountUpdated(long reportNum){
        this.reports = reportNum;
    }
}
