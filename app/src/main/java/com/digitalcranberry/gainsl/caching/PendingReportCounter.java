package com.digitalcranberry.gainsl.caching;

import android.content.Context;

import com.digitalcranberry.gainsl.model.events.PendingReportCountUpdated;

import de.greenrobot.event.EventBus;

/**
 * Created by yo on 07/07/15.
 */
public class PendingReportCounter {
    public static void updatePendingReportCount(Context context, ReportCacheManager cacheManager){
        long numOfReports = cacheManager.getNumOfReports(CacheDbConstants.UnsentReportEntry.TABLE_NAME, context);
        EventBus.getDefault().post(new PendingReportCountUpdated(numOfReports));
    }
    public static void updatePendingReportCount(Context context){
        ReportCacheManager cacheManager = new ReportCacheManager();
        long numOfReports = cacheManager.getNumOfReports(CacheDbConstants.UnsentReportEntry.TABLE_NAME, context);
        EventBus.getDefault().post(new PendingReportCountUpdated(numOfReports));
    }

}
