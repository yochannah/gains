package com.digitalcranberry.gainsl.model.events.report;

import com.digitalcranberry.gainsl.model.Report;
import com.digitalcranberry.gainsl.model.events.report.ReportEvent;

import java.util.List;

/**
 * Created by yo on 04/07/15.
 */
public class UpdatedByServer extends ReportEvent {
    public UpdatedByServer(List<Report> reports) {
        super(reports);
    }
}
