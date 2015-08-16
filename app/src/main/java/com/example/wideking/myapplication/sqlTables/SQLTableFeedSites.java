package com.example.wideking.myapplication.sqlTables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.wideking.myapplication.Helper.SQLDBHelper;
import com.example.wideking.myapplication.feed.FeedItem;
import com.example.wideking.myapplication.sqlCommands.SQLCommandsFeedSites;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by WideKing on 29.1.2015..
 */
public class SQLTableFeedSites implements BaseColumns {


    public static void addSite(FeedItem feed, Context context)//Method for adding Sites into db.
    {
        SQLDBHelper.getInstance(context);


        Cursor db_item = SQLDBHelper.db.query(SQLCommandsFeedSites.TABLE_NAME, null, SQLCommandsFeedSites.COLUMN_NAME_TITLE + " = '" + feed.getFeedName() + " '", null, null, null, null);// get row with feed's name
        if (db_item.getCount() != 0) {//This shouldn't ever happen becouse of checks.


        } else {//if row doesn't exist do this
            Log.d("TAG_DB", "nema u bazi toga");
            ContentValues values = new ContentValues();//Filling row data
            values.put(SQLCommandsFeedSites.COLUMN_NAME_TITLE, feed.getFeedName());
            values.put(SQLCommandsFeedSites.COLUMN_NAME_URL, feed.getFeedURL());
            values.put(SQLCommandsFeedSites.COLUMN_CATEGORY, feed.getCategory());
            values.put(SQLCommandsFeedSites.COLUMN_USE_FEED, feed.getUseFeed());//if use is true 1 will be returned; if it's false 0 will be returned
            values.put(SQLCommandsFeedSites.COLUMN_UPDATE_DATE, feed.getTimeInString());

            SQLDBHelper.db.insert(SQLCommandsFeedSites.TABLE_NAME, null, values);


        }

        db_item.close();
    }

    /**
     * Method that retrieves String ArrayList of user selected feed.
     *
     * @param context Provide application context
     * @return Returns String ArrayList of user selected feed.
     */
    public static ArrayList<FeedItem> getSelectedSites(Context context) {
        ArrayList<FeedItem> selectedSites = new ArrayList<>();
        SQLDBHelper.getInstance(context);

        Cursor db_item = SQLDBHelper.db.query(SQLCommandsFeedSites.TABLE_NAME, null, SQLCommandsFeedSites.COLUMN_USE_FEED + " = " + Integer.valueOf(1), null, null, null, null);
        if (db_item.moveToFirst()) {
            do {

                String time = db_item.getString(4);
                Log.d("TAG_SQLFeedGetSites", time);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date updateDate = dateFormat.parse(time);
                    FeedItem feedItemItem = new FeedItem(db_item.getString(1), db_item.getString(2), db_item.getString(3), updateDate, db_item.getInt(5));
                    selectedSites.add(feedItemItem);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            } while (db_item.moveToNext());
        }
        db_item.close();

        return selectedSites;
    }

    /**
     * Method that retrives String ArrayList of Categories that are stored inside  selected feed.
     *
     * @param context Provide application context.
     * @return Returns String ArrayList of Categories that are stored inside  selected feed.
     */
    public static ArrayList<String> getCategories(Context context) {
        ArrayList<String> Categories = new ArrayList<>();
        SQLDBHelper.getInstance(context);
        Cursor db_item = SQLDBHelper.db.query(SQLCommandsFeedSites.TABLE_NAME, null, SQLCommandsFeedSites.COLUMN_USE_FEED + " = " + Integer.valueOf(1), null, null, null, null);
        if (db_item.moveToFirst()) {
            do {
                boolean categoryExist = false;
                String category = new String();

                category = db_item.getString(3);

                for (String storedCategory : Categories) {
                    if (category.equals(storedCategory)) {
                        categoryExist = true;

                    }
                }
                if (!categoryExist) {
                    Categories.add(category);
                }
            } while (db_item.moveToNext());
        }
        db_item.close();

        return Categories;
    }

    /**
     * Method that retrieves String ArrayList of all feed sites in DB.
     *
     * @param context Provide application context
     * @return Returns String ArrayList of all feed sites in DB.
     */
    public static ArrayList<FeedItem> getAllSites(Context context) {
        ArrayList<FeedItem> allSites = new ArrayList<>();
        SQLDBHelper.getInstance(context);

        Cursor db_item = SQLDBHelper.db.query(SQLCommandsFeedSites.TABLE_NAME, null, null, null, null, null, null);
        if (db_item.moveToFirst()) {
            do {

                String time = db_item.getString(4);
                Log.d("TAG_SQLFeedGetSites", time);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date updateDate = dateFormat.parse(time);
                    FeedItem feedItemItem = new FeedItem(db_item.getString(1), db_item.getString(2), db_item.getString(3), updateDate, db_item.getInt(5));
                    allSites.add(feedItemItem);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            } while (db_item.moveToNext());
        }
        db_item.close();

        return allSites;
    }

    public static void updateFeedItem(FeedItem feed, Context context)//Method for adding Sites into db.
    {
        SQLDBHelper.getInstance(context);

        ContentValues values = new ContentValues();//Filling row data
        values.put(SQLCommandsFeedSites.COLUMN_NAME_TITLE, feed.getFeedName());
        values.put(SQLCommandsFeedSites.COLUMN_NAME_URL, feed.getFeedURL());
        values.put(SQLCommandsFeedSites.COLUMN_CATEGORY, feed.getCategory());
        values.put(SQLCommandsFeedSites.COLUMN_USE_FEED, feed.getUseFeed());//if use is true 1 will be returned; if it's false 0 will be returned
        values.put(SQLCommandsFeedSites.COLUMN_UPDATE_DATE, feed.getTimeInString());
        SQLDBHelper.db.update(SQLCommandsFeedSites.TABLE_NAME, values, SQLCommandsFeedSites.COLUMN_NAME_URL + " =" + "'" + feed.getFeedURL() + "'", null);


    }

    public static void deleteFeed(String feedUrl, Context ctx) {
        SQLDBHelper.getInstance(ctx);
        SQLDBHelper.db.delete(SQLCommandsFeedSites.TABLE_NAME, SQLCommandsFeedSites.COLUMN_NAME_URL + " =" + "'" + feedUrl + "'", null);

    }

    public static FeedItem getFeedByURL(String url, Context ctx) {
        SQLDBHelper.getInstance(ctx);
        FeedItem feed = new FeedItem("", "", "", new Date(0), 0);
        Cursor db_item = SQLDBHelper.db.query(SQLCommandsFeedSites.TABLE_NAME, null, SQLCommandsFeedSites.COLUMN_NAME_URL + " = " + "'" + url + "'", null, null, null, null);
        if (db_item.moveToFirst()) {
            String time = db_item.getString(4);
            Log.d("TAG_SQLFeedGetSites", time);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date updateDate = dateFormat.parse(time);

                feed.setFeedName(db_item.getString(1));
                feed.setFeedURL(db_item.getString(2));
                feed.setCategory(db_item.getString(3));
                feed.setUpdateTime(updateDate);
                feed.setUseFeed(db_item.getInt(5));
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
        return feed;

    }

}
