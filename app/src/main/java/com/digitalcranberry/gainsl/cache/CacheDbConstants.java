package com.digitalcranberry.gainsl.cache;

import android.provider.BaseColumns;

/**
 * Created by yo on 05/06/15.
 */
public class CacheDbConstants {
    public static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = ",";

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ReportEntry.TABLE_NAME + " (" +
                    ReportEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ReportEntry.COL_NAME_CONTENT + TEXT_TYPE + COMMA_SEP +
                    ReportEntry.COL_NAME_DATE + TEXT_TYPE + COMMA_SEP +
                    ReportEntry.COL_NAME_STATUS + TEXT_TYPE + COMMA_SEP +
                    ReportEntry.COL_NAME_LATITUDE + TEXT_TYPE + COMMA_SEP +
                    ReportEntry.COL_NAME_LONGITUDE + TEXT_TYPE + COMMA_SEP +
                    ReportEntry.COL_NAME_IMAGEURI + TEXT_TYPE + COMMA_SEP +
                    " )";

    public static abstract class ReportEntry implements BaseColumns {
        public static final String TABLE_NAME = "REPORTS";

        public static final String COL_NAME_REPORT_ID = "ID";
        public static final String COL_NAME_CONTENT = "CONTENT";
        public static final String COL_NAME_DATE = "DATE";
        public static final String COL_NAME_STATUS = "STATUS";
        public static final String COL_NAME_LATITUDE = "LATITUDE";
        public static final String COL_NAME_LONGITUDE = "LONGITUDE";
        public static final String COL_NAME_IMAGEURI = "IMAGEURI";
    }

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ReportEntry.TABLE_NAME;

}
