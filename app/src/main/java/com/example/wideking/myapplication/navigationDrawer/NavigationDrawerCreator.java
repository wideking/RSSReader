package com.example.wideking.myapplication.navigationDrawer;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.widget.ListView;

import com.example.wideking.myapplication.R;

import java.util.ArrayList;

/**
 * Class for creating Custom Navigation Drawer.
 * Created by widek on 24.7.2015..
 */
public class NavigationDrawerCreator {
    private int numberOfFrames;
    private ArrayList<NavigationDrawerItem> drawerItem;
    private DrawerLayout drawerLayout;
    private ListView lvDrawerList;
    private Context context;


    /**
     * Constructor that calls generateDrawerItemArrayList(ArrayList<String> categories) method.
     *
     * @param categories   ArrayList<String> type parameter that contains list of categories stored in table FeedSites.
     * @param drawerLayout
     * @param lvDrawerList
     */
    public NavigationDrawerCreator(ArrayList<String> categories, DrawerLayout drawerLayout, ListView lvDrawerList, Context context) {
        this.drawerLayout = drawerLayout;
        this.lvDrawerList = lvDrawerList;
        this.context = context;
        generateDrawerItemArrayList(categories);


    }

    public ArrayList<NavigationDrawerItem> getDrawerItem() {
        return drawerItem;
    }

    /**
     * Method that Creates DrawerArrayList from categories inside
     *
     * @param categories
     */

    private void generateDrawerItemArrayList(ArrayList<String> categories) {
        numberOfFrames = categories.size();
        drawerItem = new ArrayList<>();
        drawerItem.add(new NavigationDrawerGroupName(100, "Category"));
        drawerItem.add(new NavigationDrawerGroupElement(101, "Newest news", true));//Default category
        for (int i = 0; i < numberOfFrames; i++) {
            drawerItem.add(new NavigationDrawerGroupElement(102 + i, categories.get(i), true));

        }

        drawerItem.add(new NavigationDrawerGroupName(200, "Settings"));//Settings category that should be displayed last.
        drawerItem.add(new NavigationDrawerGroupElement(201, context.getResources().getStringArray(R.array.string_settings)[0], true));//Default category
        drawerItem.add(new NavigationDrawerGroupElement(202, context.getResources().getStringArray(R.array.string_settings)[1], true));//Default category
        drawerItem.add(new NavigationDrawerGroupElement(203, context.getResources().getStringArray(R.array.string_settings)[2], true));//Default category
    }


}


