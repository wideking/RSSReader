package com.example.wideking.myapplication.parser;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.example.wideking.myapplication.R;
import com.example.wideking.myapplication.activities.MainActivity;
import com.example.wideking.myapplication.feed.FeedItem;
import com.example.wideking.myapplication.sqlTables.SQLTableUserSettings;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Created by WideKing on 2.1.2015..
 * Class that handles parsing and updating feed news from net. Class has inner AsyncTask class that displays notification to user while update is in progress.Inside of AsyncTask new threads are created and places in executor.
 *
 */
public class XMLParser {

    final int THUMBNAIL = 170;
    public boolean isTest = false;

    MainActivity mainActivity;
    ArrayList<FeedItem> feedList;
    String pic_path;
    CountDownLatch countDownLatch;


    public XMLParser(MainActivity mainActivity, ArrayList<FeedItem> feedList) {
        this.mainActivity = mainActivity;
        this.feedList = feedList;
        pic_path = mainActivity.getApplicationContext().getFilesDir() + "/Images";
    }


    public void refresh() {
        AsyncMasterTask masterTask = new AsyncMasterTask();
        masterTask.execute();
    }


    private ArrayList<FeedItem> getListOfFeedsToUpdate() {
        Date currentTime = new Date();
        String updateInterval = mainActivity.getResources().getStringArray(R.array.Update_interval)[0];//This should return first item inside the array.
        int interval = Integer.parseInt(SQLTableUserSettings.getSettingsValue(updateInterval, mainActivity.getApplicationContext()));//Read update interval from DB.
        ArrayList<FeedItem> feedToUpdate = new ArrayList<>();
        for (FeedItem feed : feedList) {
            if (currentTime.getTime() - feed.getUpdateTime().getTime() >= interval * 60 * 1000) {
                Log.d("TAG_XML", "Enough time has passed since last update");
                feedToUpdate.add(feed);

            }

        }
        return feedToUpdate;
    }


    private class AsyncMasterTask extends AsyncTask<String, Integer, Boolean> {
        ArrayList<FeedItem> feedToUpdate = getListOfFeedsToUpdate();
        @Override
        protected void onPreExecute() { //Setting up UI
            mainActivity.progressBar.setVisibility(View.VISIBLE);
            mainActivity.progressBar.setMax(feedToUpdate.size());
            mainActivity.progressText.setVisibility(View.VISIBLE);
            //mainActivity.lv_layout.setVisibility(View.GONE);


            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d("TAG_onProgressUpdate", "progress" + values[0]);
            mainActivity.progressBar.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            startParsing();
            return true;
        }

        /**
         * Method for generating new worker threads that will parse the feed. AsyncTask will be paused while threads work.
         */
        private void startParsing() {
            ExecutorService taskExecutor = Executors.newFixedThreadPool(feedToUpdate.size());
            countDownLatch = new CountDownLatch(feedToUpdate.size());
            for (FeedItem feed : feedToUpdate) {
                taskExecutor.execute(new ParsingTask(mainActivity.getApplicationContext(), feed, countDownLatch));
            }
            taskExecutor.shutdown();//This disables Executors ability to get new task. Executor will finish scheduled tasks before shutting down.

            try {


                while (countDownLatch.getCount() != 0) {
                    Log.d("TAG_XMLcountDownLatch", "count down latch : " + countDownLatch.getCount());
                    int progress = (int) (feedToUpdate.size() - countDownLatch.getCount());
                    publishProgress(progress);
                    countDownLatch.await(500, TimeUnit.MILLISECONDS);//wait for 0.5 sec.
                }
                //countDownLatch.await();//Wait for all tasks to finish.
                Log.d("TAG_XMLParsingIntExc", "All task finished!");
            } catch (InterruptedException E) {
                Log.e("TAG_XMLParsingIntExc", E.getLocalizedMessage());


            }

        }



        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);

            mainActivity.progressBar.setVisibility(View.GONE);
            mainActivity.progressText.setVisibility(View.GONE);
        }
    }


}








