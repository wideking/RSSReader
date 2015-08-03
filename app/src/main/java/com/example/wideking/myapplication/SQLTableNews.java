package com.example.wideking.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SQLTableNews {


    protected static void addNews(ArrayList<News> news, Context ctx, String tableName) {
        createNewsTable(ctx, tableName);
        ContentValues values = new ContentValues();//one contentValues object for NewsList
        Log.d("TAG_DB_NewsSize", Integer.toString(news.size()));
        for (int i = 0; i < news.size(); i++) {//put news into db in order.
            values.clear();
            values.put("title", (news.get(i)).getTitle());
            values.put("category", (news.get(i)).getCategory());
            values.put("link_url", (news.get(i)).getLink_url());
            values.put("description", (news.get(i)).getDescription());
            Log.d("TAG_NameOfNews", (news.get(i)).getTitle());
            values.put("picture_url", (news.get(i)).getPicture_url().toString());
            values.put("time", (news.get(i)).getTimeInString());
            values.put("feed_name", (news.get(i).getFeedName()));
            SQLDBHelper.db.insert("News_" + tableName, null, values);

        }
    }


    protected static ArrayList<News> getNews(Context ctx, String tableName) {
        ArrayList<News> newsList = new ArrayList<>();

        Cursor db_item = SQLDBHelper.db.query("News_" + tableName, null, null, null, null, null, SQLCommandsNewsFeed.COLUMN_TIME + " DESC ");
        if (db_item.moveToFirst())//If row exist do following
        {
            do {//Create news object with information from db.

                String title = db_item.getString(1);
                String description = db_item.getString(2);
                String category = db_item.getString(3);
                String linkUrl = db_item.getString(4);
                String pictureUrl = db_item.getString(5);
                String stringTime = db_item.getString(6);
                String feedName = db_item.getString(7);

                SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date;
                try {

                    date = sqlDateFormat.parse(stringTime);
                    News news = new News(category, title, description, Uri.parse(pictureUrl), linkUrl, date, feedName);
                    newsList.add(news);
                } catch (ParseException e) {
                    Log.d("TAG_timeException", "Problem with converting time");
                    e.printStackTrace();
                }


            } while (db_item.moveToNext());
        }
        db_item.close();

        return newsList;
    }

    private static void createNewsTable(Context ctx, String tableName) {
        SQLDBHelper.getInstance(ctx);

        SQLDBHelper.db.execSQL(SQLCommandsNewsFeed.getCreateTable(tableName));
    }

    public static void deleteNewsTable(String tableName, Context ctx) {
        SQLDBHelper.getInstance(ctx);
        SQLDBHelper.db.delete("News_" + tableName, null, null);

    }
}
