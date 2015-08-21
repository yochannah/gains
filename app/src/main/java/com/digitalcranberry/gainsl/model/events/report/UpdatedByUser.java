package com.digitalcranberry.gainsl.model.events.report;

import com.digitalcranberry.gainsl.model.Report;

/**
 * Created by yo on 21/08/15.
 */
public class UpdatedByUser {
    public final Report report;
    public UpdatedByUser(Report report) {
        this.report = report;
    }

}
