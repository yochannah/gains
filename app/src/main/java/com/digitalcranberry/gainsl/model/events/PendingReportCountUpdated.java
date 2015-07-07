package com.digitalcranberry.gainsl.model.events;

import android.util.Log;

/**
 * Created by yo on 07/07/15.
 */
public class PendingReportCountUpdated {
    public long reports;
    public PendingReportCountUpdated(long reportNum){
        Log.i("SDFSDFSFSf","Hello, we're updating the counter to " + reportNum);
        this.reports = reportNum;
    }
}
