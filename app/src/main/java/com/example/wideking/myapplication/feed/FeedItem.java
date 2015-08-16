package com.example.wideking.myapplication.feed;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by widek on 24.7.2015..
 */
public class FeedItem {
    private String feedName;
    private String feedURL;
    private String category;
    private Date updateTime;
    private int useFeed;


    public FeedItem(String feedName, String feedURL, String category, Date updateTime, int useFeed) {

        this.feedName = feedName;
        this.feedURL = feedURL;
        this.category = category;
        this.updateTime = updateTime;
        this.useFeed = useFeed;

    }

    public int getUseFeed() {
        return useFeed;
    }

    public void setUseFeed(int useFeed) {
        this.useFeed = useFeed;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getFeedName() {
        return feedName;
    }

    public void setFeedName(String feedName) {
        this.feedName = feedName;
    }

    public String getFeedURL() {
        return feedURL;
    }

    public void setFeedURL(String feedURL) {
        this.feedURL = feedURL;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTimeInString() {
        String stringTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(updateTime);


        return stringTime;
    }
}
