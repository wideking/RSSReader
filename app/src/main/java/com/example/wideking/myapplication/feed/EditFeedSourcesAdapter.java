package com.example.wideking.myapplication.feed;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wideking.myapplication.Helper.DataHelper;
import com.example.wideking.myapplication.R;
import com.example.wideking.myapplication.sqlTables.SQLTableFeedSites;

import java.util.ArrayList;

/**
 * Created by widek on 29.7.2015..
 */

public class EditFeedSourcesAdapter extends ArrayAdapter<FeedItem> {
    static private ArrayList<FeedItem> feedList;
    private Context context;
    /**
     * onClickListener that focuses editable. If editText is clicked or AutoCompleteTextView or CheckBox, focus will be setted on them.If its clicked on side, keyboard will disapear and focus is set on layout. Besides this it process click on Buttons.
     * http://stackoverflow.com/questions/2679948/focusable-edittext-inside-listview
     */
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            switch (id) {
                case R.id.et_settings_edit_feed_source_name:
                    Log.d("TAG_EditSrc_FocusList", "View et_settings_edit_feed_source_name");
                    view.requestFocus();


                    break;
                case R.id.actv_settings_edit_feed_source_category:
                    Log.d("TAG_EditSrc_FocusList", "View actv_settings_edit_feed_source_category:");

                    view.requestFocus();

                    break;
                case R.id.cb_settings_edit_feed_source_use_feed:
                    Log.d("TAG_EditSrc_FocusList", "View cb_settings_edit_feed_source_use_feed:");

                    view.requestFocus();


                    break;
                case R.id.btn_save:
                    Log.d("TAG_EditSrc_FocusList", "View btn_save:");
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    if (isDataChanged(view)) {

                        Toast toast = Toast.makeText(context, context.getResources().getString(R.string.string_edit_feed_data_changed), Toast.LENGTH_SHORT);

                        toast.show();

                    } else {
                        Toast toast = Toast.makeText(context, context.getResources().getString(R.string.string_edit_feed_data_not_changed), Toast.LENGTH_SHORT);
                        toast.show();

                    }

                    break;
                case R.id.btn_delete:
                    Log.d("TAG_EditSrc_FocusList", "View btn_delete");
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    TextView tv_url = (TextView) findByIdRecursively(view, R.id.tv_settings_edit_feed_source_url_value);
                    final String url = tv_url.getText().toString();

                    AlertDialog.Builder adb = new AlertDialog.Builder(
                            context);
                    String message = context.getResources().getString(R.string.delete_feed_text);
                    String positiveMessage = context.getResources().getString(R.string.delete_feed_button_yes);
                    String negativeMessage = context.getResources().getString(R.string.cancel);
                    adb.setMessage(message + url);
                    adb.setPositiveButton(positiveMessage, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DataHelper.deleteFeed(url, context);
                            if (!DataHelper.doesFeedExist(url, context)) {
                                deleteFeed(url);
                                Toast toast = Toast.makeText(context, context.getResources().getString(R.string.feed_deleted_successfully), Toast.LENGTH_SHORT);

                                toast.show();
                            } else {
                                Toast toast = Toast.makeText(context, context.getResources().getString(R.string.feed_deleted_failed), Toast.LENGTH_LONG);

                                toast.show();

                            }

                        }
                    });
                    adb.setNegativeButton(negativeMessage, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });


                    adb.show();

                    break;


                default:
                    Log.d("TAG_EditSrc_FocusList", "View default");
                    if (!view.isFocused()) {//it can be anywhere.

                        Log.d("TAG_EditSrc_FocusList", "View listViewFocused");

                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        LinearLayout ll_test = (LinearLayout) findByIdRecursively(view, R.id.lv_settings_edit_sources_item);
                        ll_test.requestFocus();


                    }
            }


        }

    };
    private View convertView;

    public EditFeedSourcesAdapter(Context context, ArrayList<FeedItem> feedList) {
        super(context, 0);
        this.context = context;
        EditFeedSourcesAdapter.feedList = feedList;


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

            convertView = View.inflate(context, R.layout.settings_edit_feed_sources_item_layout, null);
            listViewHolder.layout = (LinearLayout) convertView.findViewById(R.id.lv_settings_edit_sources_item);

            listViewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_settings_edit_feed_source_name);
            listViewHolder.url = (TextView) convertView.findViewById(R.id.tv_settings_edit_feed_source_url);
            listViewHolder.tv_category = (TextView) convertView.findViewById(R.id.tv_settings_edit_feed_source_category);
            listViewHolder.tv_useFeed = (TextView) convertView.findViewById(R.id.tv_settings_edit_feed_source_use_feed);
            listViewHolder.urlValue = (TextView) convertView.findViewById(R.id.tv_settings_edit_feed_source_url_value);
            listViewHolder.et_name = (EditText) convertView.findViewById(R.id.et_settings_edit_feed_source_name);
            listViewHolder.actv_category = (AutoCompleteTextView) convertView.findViewById(R.id.actv_settings_edit_feed_source_category);
            listViewHolder.cb_useFeed = (CheckBox) convertView.findViewById(R.id.cb_settings_edit_feed_source_use_feed);
            listViewHolder.btnSave = (Button) convertView.findViewById(R.id.btn_save);
            listViewHolder.btnDelete = (Button) convertView.findViewById(R.id.btn_delete);


            convertView.setTag(listViewHolder);
        } else {
            listViewHolder = (ListViewHolder) convertView.getTag();

        }

        listViewHolder.tv_name.setText(convertView.getResources().getStringArray(R.array.string_edit_sources)[0]);
        listViewHolder.et_name.setText(feedList.get(position).getFeedName());
        listViewHolder.url.setText(convertView.getResources().getStringArray(R.array.string_edit_sources)[1]);
        listViewHolder.urlValue.setText(feedList.get(position).getFeedURL());
        listViewHolder.tv_category.setText(convertView.getResources().getStringArray(R.array.string_edit_sources)[2]);

        ArrayList<String> categories = DataHelper.getAllCategories(parent.getContext());
        ArrayAdapter<String> categoriesAutoCompleteAdapter = new ArrayAdapter<String>(parent.getContext(), R.layout.category_spinner_item, categories);

        listViewHolder.actv_category.setAdapter(categoriesAutoCompleteAdapter);
        listViewHolder.actv_category.setText(feedList.get(position).getCategory());
        listViewHolder.tv_useFeed.setText(convertView.getResources().getStringArray(R.array.string_edit_sources)[3]);
        listViewHolder.cb_useFeed.setChecked(feedList.get(position).getUseFeed() == 1);//if this useFeed is 1 this will return true.

        listViewHolder.et_name.setOnClickListener(onClickListener);
        listViewHolder.btnSave.setOnClickListener(onClickListener);
        listViewHolder.btnDelete.setOnClickListener(onClickListener);

        listViewHolder.layout.setOnClickListener(onClickListener);

        return convertView;

    }

    @Override
    public int getCount() {
        return feedList.size();
    }

    /**
     * Method that reads editable layout fields and checks if data is the same as in DB.
     *
     * @return Returns boolean value. It will return false if data is same as in DB. If data is not same as in DB it will return true.
     */
    private boolean isDataChanged(View view) {


        boolean isDataChanged = false;
        for (FeedItem feed : feedList) {


            EditText et_test_name = (EditText) findByIdRecursively(view, R.id.et_settings_edit_feed_source_name);
            TextView tv_settings_edit_feed_source_url_value = (TextView) findByIdRecursively(view, R.id.tv_settings_edit_feed_source_url_value);
            AutoCompleteTextView actv_category = (AutoCompleteTextView) findByIdRecursively(view, R.id.actv_settings_edit_feed_source_category);
            CheckBox cb_use_feed = (CheckBox) findByIdRecursively(view, R.id.cb_settings_edit_feed_source_use_feed);
            String name;
            String url = tv_settings_edit_feed_source_url_value.getText().toString();
            if (url.equals(feed.getFeedURL())) {
                name = et_test_name.getText().toString();
                int use = cb_use_feed.isChecked() ? 1 : 0;
                if (!(name.equals(feed.getFeedName()))) {
                    Log.d("TAG_editFeedDataChange", "readedName=" + et_test_name.getText().toString());
                    Log.d("TAG_editFeedDataChange", "storedName=" + feed.getFeedName());
                    isDataChanged = true;


                }
                if (!(actv_category.getText().toString().equals(feed.getCategory()))) {
                    Log.d("TAG_editFeedDataChange", "category changed");
                    isDataChanged = true;

                }

                if (!(use == (feed.getUseFeed()))) {
                    Log.d("TAG_editFeedDataChange", "use changed");
                    isDataChanged = true;

                }
                if (isDataChanged) {

                    String category = actv_category.getText().toString();

                    feedList.get(feedList.indexOf(feed)).setFeedName(name);
                    feedList.get(feedList.indexOf(feed)).setCategory(category);
                    feedList.get(feedList.indexOf(feed)).setUseFeed(use);
                    notifyDataSetChanged();
                    FeedItem updatedFeedItem = new FeedItem(name, tv_settings_edit_feed_source_url_value.getText().toString(), category, feed.getUpdateTime(), use);
                    SQLTableFeedSites.updateFeedItem(updatedFeedItem, context);
                }


            }

        }

        return isDataChanged;
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

    /**
     * Method that removes feed entry from arraylist.
     *
     * @param url String URL parameter.
     */
    private void deleteFeed(String url) {
        int position = -1;
        for (FeedItem feed : feedList) {
            if (url.equals(feed.getFeedURL())) {//This is for removing data from arraylist.

                position = feedList.indexOf(feed);
            }

        }
        if (position > -1) {
            feedList.remove(position);
            notifyDataSetChanged();
        }

    }

    /**
     * https://dzone.com/articles/list-editable-textboxes
     */
    private static class ListViewHolder {
        LinearLayout layout;
        TextView tv_name;
        EditText et_name;
        TextView url;
        TextView urlValue;
        TextView tv_category;
        AutoCompleteTextView actv_category;
        TextView tv_useFeed;
        CheckBox cb_useFeed;
        Button btnSave;
        Button btnDelete;


    }
}

