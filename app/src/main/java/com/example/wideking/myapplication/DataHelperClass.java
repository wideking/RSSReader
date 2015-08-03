package com.example.wideking.myapplication;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Helper class that stores all information from DB. This class is used for sending data between activities.
 * Created by widek on 24.7.2015..
 */
public class DataHelperClass {
    private static ArrayList<FeedItem> feedItem = new ArrayList<>();
    private static ArrayList<News> newsList = new ArrayList<>();


    /**
     * Private method that reads FeedSites table from SQL DB and stores information to the ArrayList<FeedItem>.
     *
     * @param context Provide application context.
     */
    private static void initializeFeedList(Context context) {
        feedItem = SQLTableFeedSites.getSelectedSites(context);

    }

    /**
     * Public method that returns ArrayList<FeedItem>. This method will call initializeFeedList  method that will get information from DB.
     *
     * @param context Provide application context.
     * @return ArrayList<FeedItem>.
     */
    public static ArrayList<FeedItem> getSelectedFeed(Context context) {
        initializeFeedList(context);
        return feedItem;
    }

    /**
     * Method that reads news from DB based on Feed's category.All news with same category are stored inside ArrayList<News> and sorted by the date.
     *
     * @param context Provide application context.
     * @return Returns sorted news for given category.
     */
    public static ArrayList<News> getNewsListByCategory(String category, Context context) {


        initializeFeedList(context);
        Log.d("TAG_DataHelperCategory", category);

        newsList.clear();//Reset data.
        for (FeedItem feed : feedItem) {
            Log.d("TAG_DataHelpCatFeed", feed.getCategory());

            if (category.equals(feed.getCategory())) {//If category from DB corresponds to wanted category, load news.

                newsList.addAll(SQLTableNews.getNews(context, parseTableName(feed.getFeedURL())));
            }

        }

        Collections.sort(newsList, new NewsComparator());//Sorting news by date.
        return newsList;


    }

    public static String parseTableName(String url) {//Method that parse Table name from feed URL.
        String parsedName = url.substring(url.indexOf(".") + 1);
        parsedName = parsedName.replace(".", "_");
        parsedName = parsedName.replace("/", "_");
        return parsedName;
    }

    public static ArrayList<FeedItem> getAllFeeds(Context context) {
        ArrayList<FeedItem> feeds = SQLTableFeedSites.getAllSites(context);
        return feeds;
    }

    public static ArrayList<News> getNewsListByFeed(Context context, String feedName) {

        initializeFeedList(context);


        newsList.clear();//Reset data.
        for (FeedItem feed : feedItem) {
            if (feedName.equals(feed.getFeedName())) {//If FeedName from DB corresponds to wanted FeedName, load news.

                newsList.addAll(SQLTableNews.getNews(context, parseTableName(feed.getFeedURL())));
            }

        }
        return newsList;

    }

    /**
     * Method that returns FeedItem item based on given name. If Item is not found it will create FeedItem object with wanted properties, but it won't save it to DB.
     *
     * @param feedName Represents name of the item inside DB.
     * @return Returns FeedItem item based on given name. If Item is not found it will create FeedItem object with wanted properties.
     */
    public static FeedItem getFeedByName(String feedName, String category, String feedURL, Context context) {

        initializeFeedList(context);

        FeedItem matchedFeed = new FeedItem("", "", "", new Date(), 0);
        for (FeedItem feed : feedItem) {
            if (feedName.equals(feed.getFeedName())) {
                matchedFeed = feed;

            }
        }
        if ("".equals(matchedFeed.getFeedName()))//FeedItem object doesn't exist, so create it.
        {


            Date updateDate = new Date(0);//Date with value 0 so feed can be updated.
            matchedFeed.setFeedName(feedName);
            matchedFeed.setCategory(category);
            matchedFeed.setFeedURL(feedURL);
            matchedFeed.setUpdateTime(updateDate);
            matchedFeed.setUseFeed(1);
        }
        return matchedFeed;
    }

    /**
     * Method that returns ArrayList<News> of news that are newer than parameter date. Method will go through all selected feedsites and return news that are newer than given date.
     *
     * @param date    Date type parameter that marks from what date should news be displayed. Eg. Display news for last 5 days.
     * @param context Provide application context.
     * @return returns  sorted by date arrayList of news.
     */
    public static ArrayList<News> getNewsListByDate(Date date, Context context) {


        initializeFeedList(context);
        Log.d("TAG_DataHelperCategory", date.toString());

        newsList.clear();//Reset data.
        for (FeedItem feed : feedItem) {
            Log.d("TAG_DataHelpCatFeed", feed.getTimeInString());
            ArrayList<News> tempNews = SQLTableNews.getNews(context, parseTableName(feed.getFeedURL()));
            for (News news : tempNews) {
                if (date.before(news.getTime())) {//If given date is before news date, news is newer than needed so put it in arraylist.
                    newsList.add(news);

                }
            }

        }


        Collections.sort(newsList, new NewsComparator());//Sorting news by date.
        return newsList;


    }

    /**
     * Method that reads categories from Feed and returns them as ArrayList<String>
     *
     * @param context Provide context.
     * @return Returns ArrayList<String> of categories for selected feeds.
     */
    public static ArrayList<String> getCategoriesFromFeed(Context context) {
        feedItem = DataHelperClass.getSelectedFeed(context);
        ArrayList<String> categories = new ArrayList<>();
        for (FeedItem item : feedItem) {

            boolean categoryExist = false;
            Log.d("TAG_getCategory", item.getCategory());
            for (String storedCategory : categories) {
                Log.d("TAG_getCategoryCat", storedCategory);
                if (item.getCategory().equals(storedCategory)) {
                    categoryExist = true;

                }
            }
            if (!categoryExist) {
                categories.add(item.getCategory());
            }

        }
        return categories;
    }

    /**
     * Method returns all categories including default category new news.
     *
     * @param context Provide context.
     * @return Returns ArrayList<String> of all categories including default category new news.
     */
    public static ArrayList<String> getAllCategories(Context context) {
        ArrayList<String> categories = getCategoriesFromFeed(context);
        String newestNewsCategory = context.getResources().getStringArray(R.array.Default_category)[1];
        categories.add(newestNewsCategory);


        return categories;
    }

    public static void deleteFeed(String url, Context context) {
        SQLTableFeedSites.deleteFeed(url, context);
        SQLTableNews.deleteNewsTable(parseTableName(url), context);

    }

    /**
     * Method for checking if feed entry and feed's news table exist.
     *
     * @param url     String parameter of url
     * @param context Provide app context.
     * @return returns true if both entry and feed's news table exist. Else returns false.
     */
    public static boolean doesFeedExist(String url, Context context) {
        boolean doesFeedExist = false;
        boolean feed_exist = false;
        boolean news_exist = false;
        FeedItem feed = SQLTableFeedSites.getFeedByURL(url, context);
        ArrayList<News> newslist = SQLTableNews.getNews(context, parseTableName(url));
        if (!"".equals(feed.getFeedName())) {
            feed_exist = true;

        }
        if (!newslist.isEmpty()) {
            news_exist = true;
        }

        if (news_exist && feed_exist) {
            doesFeedExist = true;
        }
        return doesFeedExist;


    }

    /**
     * Class that compares date of the news and returns integer value.
     */
    private static class NewsComparator implements Comparator<News> {


        @Override
        public int compare(News firstNews, News secondNews) {
            return firstNews.getTime().compareTo(secondNews.getTime());
        }
    }

}
