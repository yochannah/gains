package com.digitalcranberry.gainsl.caching;

import android.provider.BaseColumns;

/**
 * Created by yo on 05/06/15.
 */
public class CacheDbConstants {
    public static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = ",";

    public static int DB_VERSION = 15;


    private static String SQL_CREATE_REPORT_TABLE = " (" +
            ReportEntry._ID + " STRING PRIMARY KEY," +
            ReportEntry.COL_NAME_CONTENT + TEXT_TYPE + COMMA_SEP +
            ReportEntry.COL_NAME_SEND_STATUS + TEXT_TYPE + COMMA_SEP +
            ReportEntry.COL_NAME_USER_STATUS + TEXT_TYPE + COMMA_SEP +
            ReportEntry.COL_NAME_LATITUDE + TEXT_TYPE + COMMA_SEP +
            ReportEntry.COL_NAME_LONGITUDE + TEXT_TYPE + COMMA_SEP +
            ReportEntry.COL_NAME_IMAGEURI + TEXT_TYPE + COMMA_SEP +
            ReportEntry.COL_NAME_DATE_CAPTURED + TEXT_TYPE + COMMA_SEP +
            ReportEntry.COL_NAME_LAST_UPDATED + TEXT_TYPE +COMMA_SEP +
            ReportEntry.COL_NAME_LAST_UPDATED_BY + TEXT_TYPE +COMMA_SEP +
            ReportEntry.COL_NAME_ASSIGNEE + TEXT_TYPE +COMMA_SEP +
            ReportEntry.COL_NAME_REPORTER + TEXT_TYPE +
            " )";

    public static final String SQL_CREATE_UNSENT_TABLE =
            "CREATE TABLE " + UnsentReportEntry.TABLE_NAME + SQL_CREATE_REPORT_TABLE;

    public static final String SQL_CREATE_SENT_TABLE =
            "CREATE TABLE " + SentReportEntry.TABLE_NAME + SQL_CREATE_REPORT_TABLE;

    public static abstract class ReportEntry implements BaseColumns {
        public static String TABLE_NAME;

        public static final String COL_NAME_CONTENT = "CONTENT";
        public static final String COL_NAME_DATE = "DATE";
        public static final String COL_NAME_SEND_STATUS = "SEND_STATUS";
        public static final String COL_NAME_USER_STATUS = "USER_STATUS";
        public static final String COL_NAME_LATITUDE = "LATITUDE";
        public static final String COL_NAME_LONGITUDE = "LONGITUDE";
        public static final String COL_NAME_IMAGEURI = "IMAGEURI";
        public static final String COL_NAME_DATE_CAPTURED = "DATE_FIRST_CAPTURED";
        public static final String COL_NAME_LAST_UPDATED = "LAST_UPDATED";
        public static final String COL_NAME_LAST_UPDATED_BY = "LAST_UPDATED_BY";
        public static final String COL_NAME_ASSIGNEE = "ASSIGNEE";
        public static final String COL_NAME_REPORTER = "REPORTER";

    }

    public static abstract class UnsentReportEntry extends ReportEntry implements BaseColumns {
        public static final String TABLE_NAME = "UNSENT_REPORTS";
    }

    public static abstract class SentReportEntry extends ReportEntry implements BaseColumns {
        public static final String TABLE_NAME = "SENT_REPORTS";
    }

    public static final String SQL_DELETE_UNSENT_ENTRIES =
            "DROP TABLE IF EXISTS " + UnsentReportEntry.TABLE_NAME;
    public static final String SQL_DELETE_SENT_ENTRIES =
            "DROP TABLE IF EXISTS " + SentReportEntry.TABLE_NAME;

    public static final String SQL_COUNT_TABLE = "select count(*) from ";

}
