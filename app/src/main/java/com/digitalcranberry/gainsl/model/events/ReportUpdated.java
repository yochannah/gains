package com.digitalcranberry.gainsl.model.events;

import com.digitalcranberry.gainsl.model.Report;

import java.util.List;

/**
 * Created by yo on 04/07/15.
 */
public class ReportUpdated {
    public final List<Report> reports;
    public ReportUpdated(List<Report> reports) {
        this.reports = reports;
    }
}
