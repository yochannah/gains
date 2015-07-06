package com.digitalcranberry.gainsl.model.events.report;

import com.digitalcranberry.gainsl.model.Report;

/**
 * Created by yo on 04/07/15.
 */
public class Created {
    public final Report report;
    public Created(Report report) {
        this.report = report;
    }
}
