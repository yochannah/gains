package com.digitalcranberry.gainsl.comms;

import com.digitalcranberry.gainsl.model.Report;

import java.util.List;

/**
 * Created by yo on 13/06/15.
 */
public interface SendReportResult {
    void updateReportList(Report report);
    void serverReports(List<Report> remoteReportsList);
}
