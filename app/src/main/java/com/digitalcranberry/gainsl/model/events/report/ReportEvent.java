package com.digitalcranberry.gainsl.model.events.report;

import com.digitalcranberry.gainsl.model.Report;

import java.util.List;

/**
 * Created by yo on 05/07/15.
 */
public class ReportEvent {
    public final List<Report> reports;
    public ReportEvent(List<Report> reports) {
        this.reports = reports;
    }
}
