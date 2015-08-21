package com.digitalcranberry.gainsl.constants;

/**
 * Created by yo on 13/06/15.
 */
public class ReportStatuses {
    public static String REPORT_SENT = "sent";
    public static String REPORT_NEW = "new";
    public static String REPORT_UPDATED = "updated";    // this is previously sent but needs an update to send.
                                                        // will resolve to 'sent' again when sent.
    public static String REPORT_UNSENT = "unsent";
    public static String REPORT_IN_PROGRESS = "in progress";
    public static String REPORT_RESOLVED = "resolved";
}
