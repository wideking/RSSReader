package com.example.wideking.myapplication.news;

import android.net.Uri;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by WideKing on 2.1.2015..
 */
public class News implements Serializable {
    String title, description, link_url, category;
    Uri picture_url;
    Date time;
    String feedName;


    public News(String category, String title, String description, Uri picture_url, String link_url, Date time, String feedName) {
        this.category = category;
        this.title = title;
        this.description = description;
        this.picture_url = picture_url;
        this.link_url = link_url;
        this.time = time;
        this.feedName = feedName;
    }

    public News() {
        category = "Empty";
        title = "Empty";
        description = "Empty";
        picture_url = Uri.EMPTY;
        link_url = "Empty";
        feedName = "Empty";

        time = new Date();

    }

    public String getFeedName() {
        return feedName;
    }

    public void setFeedName(String feedName) {
        this.feedName = feedName;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Uri getPicture_url() {
        return picture_url;
    }

    public void setPicture_url(Uri picture_url) {
        this.picture_url = picture_url;
    }

    public String getLink_url() {
        return link_url;
    }

    public void setLink_url(String link_url) {
        this.link_url = link_url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Method that returns String representation of formatted Date object. Date format is : "yyyy-MM-dd HH:mm:ss". This format is used for storing date inside DB
     *
     * @return returns String representation of Date object.
     */
    public String getTimeInString() {

        String stringTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);


        return stringTime;
    }
}

