package com.example.wideking.myapplication.parser;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.example.wideking.myapplication.Helper.DataHelper;
import com.example.wideking.myapplication.Listeners.onExceptionEventListener;
import com.example.wideking.myapplication.activities.AddNewsAlertDialog;
import com.example.wideking.myapplication.activities.AddNewsProgressDialog;
import com.example.wideking.myapplication.feed.FeedItem;
import com.example.wideking.myapplication.news.News;
import com.example.wideking.myapplication.sqlTables.SQLTableFeedSites;
import com.example.wideking.myapplication.sqlTables.SQLTableNews;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * Created by widek on 10.8.2015..
 */
public class TestParser {
    CountDownLatch countDownLatch;
    Context context;
    AddNewsProgressDialog progressDialog;
    onExceptionEventListener exceptionEventListener;
    FeedItem feed;
    ArrayList<News> newsList;
    Dialog.OnClickListener onDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            if (i == Dialog.BUTTON_POSITIVE) {
                dbStoreFeedAndNews(feed, newsList);
                dialogInterface.dismiss();

            } else {
                dialogInterface.dismiss();
            }
        }
    };

    public TestParser(FeedItem feed, Context context, onExceptionEventListener exceptionEventListener) {
        this.countDownLatch = new CountDownLatch(0);
        this.context = context;
        this.exceptionEventListener = exceptionEventListener;
        this.feed = feed;

        progressDialog = new AddNewsProgressDialog(context);
        TestParserAsyncTask testParser = new TestParserAsyncTask();
        testParser.execute(feed);


    }

    private void dbStoreFeedAndNews(FeedItem feed, ArrayList<News> news)//Method for storing news and Feed.
    {

        SQLTableFeedSites.addSite(feed, context);
        SQLTableNews.addNews(news, context, DataHelper.parseTableName(feed.getFeedURL()));
    }

    private class TestParserAsyncTask extends AsyncTask<FeedItem, Integer, Boolean[]> {


        @Override
        protected Boolean[] doInBackground(FeedItem... feedItems) {
            ParsingTask parsingTask = new ParsingTask(context, feedItems[0], countDownLatch);
            parsingTask.isTest = true;
            parsingTask.run();
            newsList = parsingTask.parsedNews;

            return parsingTask.testFlags;
        }


        @Override
        protected void onPreExecute() { //Setting up UI


            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean[] testFlags) {
            Log.d("TAG_postExecute", "Hi from post execute");
            progressDialog.dismiss();
            if (DataHelper.isURLExceptionFlag()) {
                exceptionEventListener.onExceptionEvent(DataHelper.getMessageURLExceptionFlag(), DataHelper.getProblemURLExceptionFlag());
            } else if (DataHelper.isParserExceptionFlag()) {
                exceptionEventListener.onExceptionEvent(DataHelper.getMessageParserExceptionFlag(), DataHelper.getProblemParserExceptionFlag());
            } else {
                super.onPostExecute(testFlags);
                AddNewsAlertDialog.Builder builder = new AddNewsAlertDialog.Builder(context);
                AddNewsAlertDialog alertDialog;
                builder.setMessage("Result of parsing :" + "\n Category read: " + testFlags[0].toString() + "\n Headline read : " + testFlags[1].toString() + "\nNews text read : " + testFlags[2].toString() + "\n Picture read : " + testFlags[3].toString() + "\n Link read: " + testFlags[4].toString() + "\n Feed name read: " + testFlags[5].toString() + "\n False values means that,if You add this source, selected information won't be available!");

                builder.setPositiveButton("Add source", onDialogClickListener);
                builder.setNegativeButton("Cancel", onDialogClickListener);
                builder.create();
                builder.show();


            }


        }
    }


}

