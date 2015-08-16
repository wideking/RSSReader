package com.example.wideking.myapplication.news;

/**
 * Created by WideKing on 4.1.2015..
 */

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wideking.myapplication.R;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<News> {
    Context ctx;
    ArrayList<News> list;
    ImageView iv_picture;


    public NewsAdapter(Context ctx, ArrayList<News> list) {
        super(ctx, 0);
        this.ctx = ctx;
        this.list = list;


    }

    @Override
    public int getCount() {
        return this.list.size();

    }

    @Override
    public News getItem(int position) {

        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(ctx, R.layout.item_layout, null);
        }

        News current = list.get(position);


        TextView tv_feedName = (TextView) convertView.findViewById(R.id.tv_feed_name);
        TextView tv_title = (TextView) convertView.findViewById(R.id.tv_title);
        TextView tv_description = (TextView) convertView.findViewById(R.id.tv_description);
        iv_picture = (ImageView) convertView.findViewById(R.id.iv_image);
        iv_picture.setImageURI(current.getPicture_url());
        tv_feedName.setText(current.getFeedName());
        tv_title.setText(current.getTitle());
        if (tv_description != null) {
            Log.d("TAG_NewsAdapterDesc", "text view je kreiran");
            tv_description.setText(current.getDescription());

        }
        Log.d("TAG_NewsAdapterDesc", current.getDescription());


        return convertView;
    }


}