package com.example.wideking.myapplication;

import android.provider.BaseColumns;

public class SQLCommandsNewsFeed
        implements BaseColumns {

    public static final String COLUMN_TIME = "time";
    private static String TABLE_NAME;

    public static String getCreateTable(String tableName) {
        TABLE_NAME = "News_" + tableName;
        return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + "_id" + " INTEGER PRIMARY KEY," + "title" + " TEXT," + "description" + " TEXT," + "category" + " TEXT, " + "link_url" + " TEXT, " + "picture_url" + " TEXT, " + "time" + " TEXT, " + "feed_name " + "TEXT" + " )";
    }

    public static String getDeleteTable(String tableName) {
        TABLE_NAME = "News_" + tableName;
        return "DROP TABLE IF EXIST " + TABLE_NAME;
    }
}


