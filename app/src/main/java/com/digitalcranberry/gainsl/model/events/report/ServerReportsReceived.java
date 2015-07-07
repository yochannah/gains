package com.digitalcranberry.gainsl.model.events.report;

import com.digitalcranberry.gainsl.model.Report;
import com.digitalcranberry.gainsl.model.events.report.ReportEvent;

import java.util.List;

/**
 * Created by yo on 05/07/15.
 */
public class ServerReportsReceived extends ReportEvent {
    public ServerReportsReceived(List<Report> reports) {
        super(reports);
    }
}
