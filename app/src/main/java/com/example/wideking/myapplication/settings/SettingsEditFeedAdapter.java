package com.example.wideking.myapplication.settings;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.wideking.myapplication.Helper.DataHelper;
import com.example.wideking.myapplication.R;

import java.util.ArrayList;

/**
 * Created by widek on 29.7.2015..
 */
public class SettingsEditFeedAdapter extends ArrayAdapter<SettingsEditFeed> {
    ArrayList<SettingsEditFeed> settingsEditFeeds;
    Context context;
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        }
    };
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            switch (id) {
                case R.id.spinner_settings_edit_feed_settings:
                    Log.d("TAG_EditSettings", "View spinner_settings_edit_feed_settingsr:");

                    view.requestFocus();

                    break;
                case R.id.et_settings_edit_feed_settings:
                    Log.d("TAG_EditSettings", "View et_settings_edit_feed_settings:");

                    view.requestFocus();


                    break;


                default:
                    Log.d("TAG_EditSettings", "View default");
                    if (!view.isFocused()) {//it can be anywhere.

                        Log.d("TAG_EditSettings_Focus", "View listViewFocused");

                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        RelativeLayout relativeLayout = (RelativeLayout) findByIdRecursively(view, R.id.rl_settings_edit_settings);
                        relativeLayout.requestFocus();


                    }
            }


        }
    };

    public SettingsEditFeedAdapter(ArrayList<SettingsEditFeed> settingsEditFeeds, Context context) {
        super(context, 0);
        this.settingsEditFeeds = settingsEditFeeds;
        this.context = context;
    }

    /**
     * Returns recycled view of listview.
     *
     * @param position    Possition of item in listview.
     * @param convertView View that will be replaced
     * @param parent      parent view
     * @return returns convertView
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListViewHolder listViewHolder;
        if (convertView == null) {
            listViewHolder = new ListViewHolder();
            convertView = View.inflate(context, R.layout.settings_edit_feed_settings_item_layout, null);


            listViewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_settings_edit_feed_settings);
            listViewHolder.et_value = (EditText) convertView.findViewById(R.id.et_settings_edit_feed_settings);
            listViewHolder.spinner = (Spinner) convertView.findViewById(R.id.spinner_settings_edit_feed_settings);
            listViewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.rl_settings_edit_settings);


            convertView.setTag(listViewHolder);
        } else {

            listViewHolder = (ListViewHolder) convertView.getTag();

        }
        String category = context.getResources().getStringArray(R.array.Default_category)[0];
        Log.d("TAG_EditFeedBefSpinner", settingsEditFeeds.get(position).getSettingsName());
        Log.d("TAG_EditFeedBefSpinner", category);
        Log.d("TAG_EditFeedBefSpinner", Integer.toString(position));

        if (settingsEditFeeds.get(position).getSettingsName().equals(category)) {//initialize view with default category.


            String categoryValue = settingsEditFeeds.get(position).getValue();
            Log.d("TAG_EditFeedSpinner", settingsEditFeeds.get(position).getSettingsName());
            Log.d("TAG_EditFeedSpinner", Integer.toString(position));

            ArrayList<String> categories = DataHelper.getAllCategories(context);

            ArrayAdapter<String> spinnerDataAdapter = new ArrayAdapter<String>(context, R.layout.category_spinner_item, categories);

            listViewHolder.spinner.setAdapter(spinnerDataAdapter);

            int spinnerItemPosition = 0;
            for (String newCategory : categories) {
                if (categoryValue.equals(newCategory)) {

                    spinnerItemPosition = categories.indexOf(newCategory);
                }

            }
            listViewHolder.et_value.setVisibility(View.INVISIBLE);
            listViewHolder.spinner.setSelection(spinnerItemPosition);
            listViewHolder.tv_name.setText(settingsEditFeeds.get(position).getSettingsName());


        } else {//initialize other views.

            Log.d("TAG_EditFeedSetName", settingsEditFeeds.get(position).getSettingsName());
            Log.d("TAG_EditFeedSetPos", Integer.toString(position));


            listViewHolder.tv_name.setText(settingsEditFeeds.get(position).getSettingsName());
            listViewHolder.et_value.setText(settingsEditFeeds.get(position).getValue());
            listViewHolder.et_value.setOnClickListener(onClickListener);


        }
        listViewHolder.layout.setOnClickListener(onClickListener);


        return convertView;
    }

    @Override
    public int getCount() {
        return settingsEditFeeds.size();
    }

    /**
     * Method that returns view that coresponds with ID.
     *
     * @param view     view that will be searched for id
     * @param targetId search ID.
     * @return returns view that contains object with ID.
     */
    public View findByIdRecursively(View view, int targetId) {
        View result = view.findViewById(targetId);
        if (result != null) {
            return result;
        }
        View parent = (View) view.getParent();
        if (parent == null) {
            return null;
        }
        return findByIdRecursively(parent, targetId);
    }

    private static class ListViewHolder {
        RelativeLayout layout;
        TextView tv_name;
        EditText et_value;
        Spinner spinner;

    }
}
