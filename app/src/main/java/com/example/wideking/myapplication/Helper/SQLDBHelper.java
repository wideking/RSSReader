package com.example.wideking.myapplication.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.wideking.myapplication.R;
import com.example.wideking.myapplication.sqlCommands.SQLCommandsSettings;

public class SQLDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "NewsFeed.db";
    private static final int DATABASE_VERSION = Integer.valueOf(1);
    public static SQLiteDatabase db;
    private static Context context;
    private static SQLDBHelper dbInstance;

    private SQLDBHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLDBHelper.context = context;
    }

    public static SQLDBHelper getInstance(Context paramContext) {
        if (dbInstance == null) {
            dbInstance = new SQLDBHelper(paramContext);
            db = dbInstance.getWritableDatabase();
        }
        context = paramContext;
        return dbInstance;
    }

    private static void initializeSettingsTable(SQLiteDatabase db) {


        Log.d("TAG_SQLDBHelp", "This is from db");

        db.execSQL("CREATE TABLE " + SQLCommandsSettings.TABLE_NAME + " (_id INTEGER PRIMARY KEY, " + SQLCommandsSettings.TITLE + " TEXT, " + SQLCommandsSettings.VALUE + " TEXT)");
        ContentValues values = new ContentValues();
        TypedArray settingsArray = context.getResources().obtainTypedArray(R.array.string_user_settings_array);
        String[][] array = new String[settingsArray.length()][];
        for (int i = 0; i < settingsArray.length(); i++) {
            int resourceID = settingsArray.getResourceId(i, 0);
            if (resourceID > 0) {
                array[i] = context.getResources().getStringArray(resourceID);
            }

        }
        settingsArray.recycle();//Release resource so that can be used later.

        for (int i = 0; i < array.length; i++) {
            String settingName = array[i][0];
            String value = array[i][1];
            Log.d("TAG_SQLDBHelp", "This is from db: " + settingName);
            values.put(SQLCommandsSettings.TITLE, settingName);
            values.put(SQLCommandsSettings.VALUE, value);
            db.insert(SQLCommandsSettings.TABLE_NAME, null, values);
        }
    }

    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE FeedSites (_id INTEGER PRIMARY KEY,title TEXT,url TEXT,category TEXT,update_date TEXT, use INTEGER  )");
        initializeSettingsTable(db);


    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + DATABASE_NAME);
        onCreate(db);
    }
}
