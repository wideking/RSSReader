package com.example.wideking.myapplication;

/**
 * Created by widek on 23.7.2015..
 */
public class NavigationDrawerGroupElement implements NavigationDrawerItem {
    public static final int ITEM_TYPE = 1;

    private int id;
    private String label;
    private boolean updateActionBarTitle;

    public NavigationDrawerGroupElement(int id, String label, boolean updateActionBarTitle) {
        this.id = id;
        this.label = label;
        this.updateActionBarTitle = updateActionBarTitle;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public int getType() {
        return ITEM_TYPE;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean updateActionBarTitle() {
        return false;
    }
}
