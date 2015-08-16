package com.example.wideking.myapplication.sqlTables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.wideking.myapplication.Helper.SQLDBHelper;
import com.example.wideking.myapplication.settings.SettingsEditFeed;
import com.example.wideking.myapplication.sqlCommands.SQLCommandsSettings;

import java.util.ArrayList;

/**
 * Created by widek on 27.7.2015..
 */
public class SQLTableUserSettings {
    /**
     * Method for changing value of Settings.
     *
     * @param settingsName String that represents name of Setting that value is changed
     * @param value        String that represents value of Settings
     * @param context      Provide application context.
     */
    public static void ChangeSetting(String settingsName, String value, Context context) {
        SQLDBHelper.getInstance(context);
        Cursor db_item = SQLDBHelper.db.query(SQLCommandsSettings.TABLE_NAME, null, SQLCommandsSettings.TITLE + " ='" + settingsName + "'", null, null, null, null);// get row with setting's name
        if (db_item.getCount() != 0) {
            ContentValues values = new ContentValues();//Filling row data
            values.put(SQLCommandsSettings.TITLE, settingsName);
            values.put(SQLCommandsSettings.VALUE, value);
            SQLDBHelper.db.update(SQLCommandsSettings.TABLE_NAME, values, SQLCommandsSettings.TITLE + " = " + "'" + settingsName + "'", null);


        } else {//this should never happen.
            Log.d("TAG_DB", "nema u bazi toga");


        }

        db_item.close();
    }

    public static String getSettingsValue(String settingsName, Context context) {
        Log.d("TAG_SQLSettingsNAme", settingsName);

        SQLDBHelper.getInstance(context);
        Cursor db_item = SQLDBHelper.db.query(SQLCommandsSettings.TABLE_NAME, null, SQLCommandsSettings.TITLE + " ='" + settingsName + "'", null, null, null, null);// get row with setting's name

        String value = "";

        Log.d("TAG_SQLTableMove", Integer.toString(db_item.getCount()));
        if (db_item.moveToFirst()) {
            value = db_item.getString(2);

        }

        db_item.close();

        return value;

    }

    /**
     * Method that returns ArrayList<SettingEditFeed> that contains all user settings.
     *
     * @param context provide application context.
     * @return returns ArrayList<SettingEditFeed> that contains all user settings.
     */
    public static ArrayList<SettingsEditFeed> getSettings(Context context) {
        ArrayList<SettingsEditFeed> settings = new ArrayList<>();
        SQLDBHelper.getInstance(context);
        Cursor db_item = SQLDBHelper.db.query(SQLCommandsSettings.TABLE_NAME, null, null, null, null, null, SQLCommandsSettings.TITLE + " ASC ");

        if (db_item.moveToFirst()) {

            do {
                SettingsEditFeed setting = new SettingsEditFeed("", "");
                setting.setSettingsName(db_item.getString(1));
                setting.setValue(db_item.getString(2));
                settings.add(setting);

            } while (db_item.moveToNext());
        }
        return settings;

    }


}







