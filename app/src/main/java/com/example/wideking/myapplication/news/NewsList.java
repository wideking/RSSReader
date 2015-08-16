package com.example.wideking.myapplication.news;

import java.util.ArrayList;

/**
 * Created by WideKing on 9.1.2015..
 * Array that contains News feed and news.
 */
public class NewsList {
    private ArrayList<News> news = new ArrayList<News>();
    private String name;
    private String update_date;

    public NewsList(ArrayList<News> news, String name, String update_date) {
        this.news = news;
        this.name = name;
        this.update_date = update_date;

    }

    public String getUpdate_date() {

        return update_date;
    }

    public void setUpdate_date(String update_date) {
        this.update_date = update_date;
    }

    public ArrayList<News> getNews() {
        return news;
    }

    public void setNews(ArrayList<News> news) {
        this.news = news;
    }

    public String getName() {
        return name;
    }
}
