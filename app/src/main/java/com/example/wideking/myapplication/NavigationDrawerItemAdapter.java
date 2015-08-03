package com.example.wideking.myapplication;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by widek on 23.7.2015..
 * <p/>
 * http://www.michenux.net/android-navigation-drawer-748.html
 */
public class NavigationDrawerItemAdapter extends ArrayAdapter<NavigationDrawerItem> {

    private Context ctx;

    public NavigationDrawerItemAdapter(Context context, int resource, ArrayList<NavigationDrawerItem> items) {
        super(context, resource, items);
        ctx = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        NavigationDrawerItem menuItem = this.getItem(position);
        if (menuItem.getType() == NavigationDrawerGroupElement.ITEM_TYPE) {
            view = getGroupElementView(convertView, parent, menuItem);
        } else {
            view = getGroupNameView(convertView, parent, menuItem);
        }
        return view;


    }

    private View getGroupElementView(View convertView, ViewGroup parent, NavigationDrawerItem menuItem) {

        if (convertView == null) {
            convertView = View.inflate(ctx, R.layout.drawer_list_group_element, null);

        }

        TextView tv_drawer_list_item = (TextView) convertView.findViewById(R.id.tv_drawer_list_item);
        tv_drawer_list_item.setText(menuItem.getLabel());


        return convertView;
    }

    private View getGroupNameView(View convertView, ViewGroup parent, NavigationDrawerItem menuItem) {

        if (convertView == null) {
            convertView = View.inflate(ctx, R.layout.drawer_list_group_name, null);

        }

        TextView tv_drawer_list_item = (TextView) convertView.findViewById(R.id.tv_drawer_list_group_name);
        tv_drawer_list_item.setText(menuItem.getLabel());


        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return this.getItem(position).getType();
    }

    @Override
    public boolean isEnabled(int position) {
        return getItem(position).isEnabled();
    }
}
