package com.digitalcranberry.gainsl.model.events;

import com.digitalcranberry.gainsl.model.Report;

/**
 * Created by yo on 04/07/15.
 */
public class ReportSent {
    public final Report report;
    public ReportSent(Report report) {
        this.report = report;
    }
}
