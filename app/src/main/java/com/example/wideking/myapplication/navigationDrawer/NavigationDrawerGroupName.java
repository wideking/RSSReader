package com.example.wideking.myapplication.navigationDrawer;

/**
 * Created by widek on 23.7.2015..
 */
public class NavigationDrawerGroupName implements NavigationDrawerItem {
    public static final int GROUP_TYPE = 0;
    private int id;
    private String label;

    public NavigationDrawerGroupName(int id, String label) {
        this.id = id;
        this.label = label;

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
        return GROUP_TYPE;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean updateActionBarTitle() {
        return false;
    }
}
