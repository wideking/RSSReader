package com.example.wideking.myapplication;

/**
 * Created by widek on 23.7.2015..
 */
public interface NavigationDrawerItem {
    int getId();

    String getLabel();

    int getType();

    boolean isEnabled();

    boolean updateActionBarTitle();
}
