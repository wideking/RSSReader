package com.example.wideking.myapplication.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wideking.myapplication.Helper.DataHelper;
import com.example.wideking.myapplication.Listeners.onExceptionEventListener;
import com.example.wideking.myapplication.R;
import com.example.wideking.myapplication.feed.EditFeedSourcesAdapter;
import com.example.wideking.myapplication.feed.FeedItem;
import com.example.wideking.myapplication.news.NewsAdapter;
import com.example.wideking.myapplication.parser.TestParser;
import com.example.wideking.myapplication.settings.SettingsEditFeed;
import com.example.wideking.myapplication.settings.SettingsEditFeedAdapter;
import com.example.wideking.myapplication.sqlTables.SQLTableFeedSites;
import com.example.wideking.myapplication.sqlTables.SQLTableUserSettings;

import java.util.ArrayList;
import java.util.Date;

/**
 * Class for creating Fragment for displaying news.
 * Created by widek on 24.7.2015..
 */
public class ContentFragment extends Fragment implements onExceptionEventListener {


    private static EditFeedSourcesAdapter feedSourcesAdapter;
    EditText etAddFeedURL;
    EditText etAddFeedName;
    CheckBox addFeedUseFeed;
    AutoCompleteTextView addFeedCategory;
    Dialog.OnClickListener onDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    };
    private ListView lvList;
    private ListView lvSettings;
    private ListView lvEditFeedSources;
    private NewsAdapter newsAdapter;
    private ArrayList<SettingsEditFeed> settingsArray;
    private SettingsEditFeedAdapter settingsAdapter;
    private Context ctx;
    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            switch (id) {
                case R.id.rl_add_feed:
                    Log.d("TAG_OnClick", "on rl add feed");
                    view.requestFocus();
                    imm.hideSoftInputFromInputMethod(getActivity().getCurrentFocus().getWindowToken(), 0);
                    break;
                case R.id.btn_add_feed:
                    view.requestFocus();
                    imm.hideSoftInputFromInputMethod(getActivity().getCurrentFocus().getWindowToken(), 0);
                    boolean fieldInitialized = true;
                    String errorMessage = "\nFollowing problems occurred: ";
                    String category = addFeedCategory.getText().toString();

                    if ("".equals(category)) {
                        fieldInitialized = false;
                        errorMessage += "Category not set!";
                    }
                    String name = etAddFeedName.getText().toString();

                    if ("".equals(name)) {
                        fieldInitialized = false;
                        errorMessage += "\nName not set!";
                    }

                    String url = etAddFeedURL.getText().toString();

                    if ("".equals(url)) {
                        fieldInitialized = false;
                        errorMessage += "\nURL not set!";
                    }
                    boolean useFeed = addFeedUseFeed.isChecked();

                    if (fieldInitialized) {
                        FeedItem feed = new FeedItem(name, url, category, new Date(0), useFeed ? 1 : 0);
                        TestParser testParser = new TestParser(feed, ctx, getOnExceptionEventListener());
                    } else {

                        final AddNewsAlertDialog.Builder alertBuilder = new AddNewsAlertDialog.Builder(ctx);
                        alertBuilder.setMessage(errorMessage);
                        final AlertDialog alertDialog = alertBuilder.create();
                        alertBuilder.setNeutralButton("OK", new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.show();


                    }
                    break;

                case R.id.btn_settings_save:
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    checkForChanges();


                    break;
                case R.id.ll_settings_main_layout:
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    break;
            }

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ctx = container.getContext();
        View rootView = null;
        String category = getArguments().getString("category");
        Log.d("TAG_FragArg", category);
        switch (category) {
            case "Newest news":
                Log.d("TAG_newest_news", category);
                rootView = inflater.inflate(R.layout.news_fragment, container, false);
                lvList = (ListView) rootView.findViewById(R.id.lv_list_fragment);
                String settingsStringDateName = getResources().getStringArray(R.array.Load_news_newer_than)[0];
                Integer dateFromSettings = Integer.parseInt(SQLTableUserSettings.getSettingsValue(settingsStringDateName, container.getContext()));
                Log.d("TAG_dateFromSettings", dateFromSettings.toString());
                Date maxDate = new Date();
                maxDate.setTime(new Date().getTime() - dateFromSettings * 1000 * 60 * 60 * 24);//set's time to current time - days from settings.

                newsAdapter = new NewsAdapter(container.getContext(), DataHelper.getNewsListByDate(maxDate, container.getContext()));

                lvList.setAdapter(newsAdapter);
                newsAdapter.notifyDataSetChanged();

                break;

            case "Edit feeds settings":
                rootView = inflater.inflate(R.layout.settings_edit_feed_settings, container, false);
                lvSettings = (ListView) rootView.findViewById(R.id.lv_settings_edit_feed);
                settingsArray = SQLTableUserSettings.getSettings(container.getContext());
                for (SettingsEditFeed setting : settingsArray) {
                    Log.d("TAG_setting", setting.getSettingsName());
                }
                settingsAdapter = new SettingsEditFeedAdapter(settingsArray, container.getContext());

                lvSettings.setAdapter(settingsAdapter);
                lvSettings.setItemsCanFocus(true);

                settingsAdapter.notifyDataSetChanged();
                Button btn_save = (Button) rootView.findViewById(R.id.btn_settings_save);
                btn_save.setOnClickListener(onClickListener);
                LinearLayout llMainSettings = (LinearLayout) rootView.findViewById(R.id.ll_settings_main_layout);
                llMainSettings.setOnClickListener(onClickListener);

                break;
            case "Edit feeds sources":


                ArrayList<FeedItem> completeFeedList = SQLTableFeedSites.getAllSites(container.getContext());
                if (completeFeedList.isEmpty()) {//Show info that there is no feed to show.
                    rootView = inflater.inflate(R.layout.empty_fragment, container, false);

                } else {
                    rootView = inflater.inflate(R.layout.settings_edit_feed_sources, container, false);
                    lvEditFeedSources = (ListView) rootView.findViewById(R.id.lv_settings_edit_feed_sources);
                    Log.d("TAG_EditSourcesArrSize", Integer.toString(completeFeedList.size()));
                    feedSourcesAdapter = new EditFeedSourcesAdapter(container.getContext(), completeFeedList);

                    lvEditFeedSources.setAdapter(feedSourcesAdapter);
                    feedSourcesAdapter.notifyDataSetChanged();


                }


                break;
            case "Add new feed source":
                Log.d("TAG_addFeed", "hi from add feed");
                rootView = inflater.inflate(R.layout.settings_add_feed, container, false);
                TextView addFeedName = (TextView) rootView.findViewById(R.id.tv_settings_add_feed_name);
                TextView addFeedUrl = (TextView) rootView.findViewById(R.id.tv_settings_add_feed_url);
                RelativeLayout rlAddFeed = (RelativeLayout) rootView.findViewById(R.id.rl_add_feed);
                etAddFeedName = (EditText) rootView.findViewById(R.id.et_settings_add_feed_name);
                etAddFeedURL = (EditText) rootView.findViewById(R.id.et_settings_add_feed_url);
                addFeedCategory = (AutoCompleteTextView) rootView.findViewById(R.id.actv_settings_add_feed_category);
                ArrayList<String> categories = DataHelper.getAllCategories(container.getContext());
                ArrayAdapter<String> spinnerDataAdapter = new ArrayAdapter<String>(container.getContext(), R.layout.category_spinner_item, categories);
                addFeedCategory.setAdapter(spinnerDataAdapter);
                Button btnAddFeed = (Button) rootView.findViewById(R.id.btn_add_feed);
                addFeedUseFeed = (CheckBox) rootView.findViewById(R.id.cb_settings_add_feed_use_feed);

                rlAddFeed.setOnClickListener(onClickListener);
                btnAddFeed.setOnClickListener(onClickListener);


                break;
            case "Empty":
                rootView = inflater.inflate(R.layout.empty_fragment, container, false);

                break;

            default: //if string doesn't match any of previous string, it must be news category, so display news :)
                rootView = inflater.inflate(R.layout.news_fragment, container, false);
                lvList = (ListView) rootView.findViewById(R.id.lv_list_fragment);

                newsAdapter = new NewsAdapter(container.getContext(), DataHelper.getNewsListByCategory(category, container.getContext()));
                lvList.setAdapter(newsAdapter);
                newsAdapter.notifyDataSetChanged();

                break;
        }


        getActivity().setTitle(category);

        return rootView;
    }

    /**
     * Method that checks if user has changed settings. It reads TextView and spinner and matches their values with DB settings. if they dont match it will call updateSettings () method.
     */
    protected void checkForChanges() {
        TextView tv_settingsName;
        Spinner spinner;
        EditText editText;
        String category = ctx.getResources().getStringArray(R.array.Default_category)[0];
        ArrayList<SettingsEditFeed> updatedSettings = new ArrayList<>();
        boolean isDataChanged = false;
        String value;

        for (int i = 0; i < lvSettings.getCount(); i++) {//get through every view and check what is stored in view.
            View child = lvSettings.getChildAt(i);

            tv_settingsName = (TextView) child.findViewById(R.id.tv_settings_edit_feed_settings);
            String settingsName = tv_settingsName.getText().toString();

            if (category.equals(settingsName)) {//if it's category then get spinner
                spinner = (Spinner) child.findViewById(R.id.spinner_settings_edit_feed_settings);
                value = (String) spinner.getSelectedItem();


            } else {
                editText = (EditText) child.findViewById(R.id.et_settings_edit_feed_settings);
                value = editText.getText().toString();

            }
            Log.d("TAG_ContentSettingsName", settingsName);
            Log.d("TAG_ContentSettingsVal", value);
            String storedValue = SQLTableUserSettings.getSettingsValue(settingsName, ctx);
            if (!value.equals(storedValue)) {
                isDataChanged = true;
                updatedSettings.add(new SettingsEditFeed(settingsName, value));
            }


        }
        updateSettings(updatedSettings, isDataChanged);
    }

    /**
     * Method that updates settings in DB with settings from arraylist.
     *
     * @param updatedSettings Arraylist of SettingsEditFeed object that will replace settings with same name in db.
     * @param isDataChanged   flag that determens if update is necessary.If its true than update will start.
     */
    private void updateSettings(ArrayList<SettingsEditFeed> updatedSettings, boolean isDataChanged) {

        if (isDataChanged) {//if data is changed, save them to DB and change data in arraylist.
            for (SettingsEditFeed setting : updatedSettings) {
                Log.d("TAG_settingName", setting.getSettingsName());
                SQLTableUserSettings.ChangeSetting(setting.getSettingsName(), setting.getValue(), ctx);
                for (int j = 0; j < settingsArray.size(); j++) {
                    if (setting.getSettingsName().equals(settingsArray.get(j).getSettingsName())) {
                        settingsArray.get(j).setValue(setting.getValue());
                    }
                }

            }
            settingsAdapter.notifyDataSetChanged();
            Toast toast = Toast.makeText(ctx, ctx.getResources().getString(R.string.settings_updated), Toast.LENGTH_LONG);

            toast.show();

        } else {
            Toast toast = Toast.makeText(ctx, ctx.getResources().getString(R.string.settings_no_changes), Toast.LENGTH_LONG);

            toast.show();
        }
    }

    @Override
    public void onExceptionEvent(String message, String problem) {
        AddNewsAlertDialog.Builder alertBuilder = new AddNewsAlertDialog.Builder(ctx);
        alertBuilder.setMessage(message + " : " + problem);
        AlertDialog alertDialog;

        alertBuilder.setNeutralButton("Dismiss", onDialogClickListener);
        alertDialog = alertBuilder.create();
        alertDialog.show();


    }

    public ContentFragment getOnExceptionEventListener() {
        return this;

    }
}
