package com.example.wideking.myapplication.sqlCommands;

import android.provider.BaseColumns;

public class SQLCommandsFeedSites
        implements BaseColumns {
    public static final String COLUMN_CATEGORY = "category";

    public static final String COLUMN_NAME_TITLE = "title";
    public static final String COLUMN_NAME_URL = "url";
    public static final String COLUMN_UPDATE_DATE = "update_date";
    public static final String COLUMN_USE_FEED = "use";

    public static final String TABLE_NAME = "FeedSites";
}
