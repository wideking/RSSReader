package com.example.wideking.myapplication.activities;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.wideking.myapplication.Helper.DataHelper;
import com.example.wideking.myapplication.R;
import com.example.wideking.myapplication.feed.FeedItem;
import com.example.wideking.myapplication.navigationDrawer.NavigationDrawerCreator;
import com.example.wideking.myapplication.navigationDrawer.NavigationDrawerItem;
import com.example.wideking.myapplication.navigationDrawer.NavigationDrawerItemAdapter;
import com.example.wideking.myapplication.parser.XMLParser;
import com.example.wideking.myapplication.sqlTables.SQLTableUserSettings;

import java.io.File;
import java.util.ArrayList;


//TODO Implement adding new sources with check if data is parseable and check if data exist.Tell user what is the problem.
//TODO check XMLParser, impelemnt way procceed on this input. done
//TODO create Class for forking asyncTasks. http://developer.android.com/reference/android/os/AsyncTask.html#executeOnExecutor%28java.util.concurrent.Executor,%20Params...%29 http://stackoverflow.com/questions/31768081/how-to-wait-multiple-asynctask-to-finish-their-work
//TODO when changing to landscape, load fragment that was displayed.
public class MainActivity extends ActionBarActivity {
    static String URL_BUG_HR = "http://www.bug.hr/rss/vijesti/";
    static String URL_INDEX_HR = "http://www.index.hr/najnovije/rss.ashx";
    static String URL_INDEX_HR_VIJESTI = "http://www.index.hr/vijesti/rss.ashx";
    public ProgressBar progressBar;
    public TextView progressText;
    public LinearLayout lv_layout;
    ArrayList<FeedItem> feedItem = new ArrayList<>();
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressText = (TextView) findViewById(R.id.tv_loading);
        lv_layout = (LinearLayout) findViewById(R.id.lv_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);// Set the adapter for the list view


        String path = getApplicationContext().getFilesDir() + "/Images";

        File imagesFolder = new File(path);
        if (!imagesFolder.exists()) {
            imagesFolder.mkdirs();
        }

        feedItem = DataHelper.getSelectedFeed(getApplicationContext());
        if (feedItem.isEmpty())//If there are no default feeds or no db  parse this default feeds
        {


            ArrayList<String> categories = DataHelper.getCategoriesFromFeed(getApplicationContext());
            initializeDrawer(categories);
            setDisplayFragment(0, "Empty");
            ArrayList<FeedItem> testFeedList = new ArrayList<>();
            testFeedList.add(DataHelper.getFeedByName("Index vijesti", "Politics", URL_INDEX_HR_VIJESTI, getApplicationContext()));
            testFeedList.add(DataHelper.getFeedByName("Index najnovije", "Politics", URL_INDEX_HR, getApplicationContext()));
            testFeedList.add(DataHelper.getFeedByName("Bug vijesti", "Technology", URL_BUG_HR, getApplicationContext()));
            testFeedList.add(DataHelper.getFeedByName("24 sata Sport", "Sport", "http://www.24sata.hr/feeds/sport.xml", getApplicationContext()));
            testFeedList.add(DataHelper.getFeedByName("24 sata Vijesti", "Politics", "http://www.24sata.hr/feeds/news.xml", getApplicationContext()));
            XMLParser xmlParser = new XMLParser(this, testFeedList);
            xmlParser.refresh();



        } else {

            getUpdateFromRSS();
            ArrayList<String> categories = DataHelper.getCategoriesFromFeed(getApplicationContext());
            initializeDrawer(categories);
            String defaultFeedCategoryName = getResources().getStringArray(R.array.Default_category)[0];
            String defaultFeedCategoryValue = SQLTableUserSettings.getSettingsValue(defaultFeedCategoryName, getApplicationContext());
            String categoryAddFeed = getResources().getStringArray(R.array.string_settings)[2];
            int position = getCategoryPosition(categories, categoryAddFeed);
            setDisplayFragment(position, defaultFeedCategoryValue);

        }


    }


    private int getCategoryPosition(ArrayList<String> categories, String categoryAddFeed) {
        int position = 0;
        for (String category : categories) {
            if (categoryAddFeed.equals(category))
                position = categories.indexOf(category);
        }
        return position;
    }

    private void getUpdateFromRSS() {
        XMLParser xmlParser = new XMLParser(this, DataHelper.getSelectedFeed(getApplicationContext()));
        xmlParser.refresh();
    }


    public void initializeDrawer(ArrayList<String> categories) {
        NavigationDrawerCreator drawer = new NavigationDrawerCreator(categories, mDrawerLayout, mDrawerList, getApplicationContext());
        mDrawerList.setAdapter(new NavigationDrawerItemAdapter(getApplicationContext(), R.layout.activity_main, drawer.getDrawerItem()));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Method that changes current fragment with clicked fragment. Method creates new contentFragment object, adds needed parameter and calls FragmentManager to swap current fragment with new one.
     *
     * @param position int type parameter that marks position of clicked item in drawer listview.
     * @param category String type parameter that marks name(category of news) of the clicked item.
     */
    private void setDisplayFragment(int position, String category) {

        ContentFragment contentFragment = new ContentFragment();
        Bundle args = new Bundle();
        Log.d("TAG_FragSendArg", category);
        args.putString("category", category);
        contentFragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, contentFragment)
                .commit();


        mDrawerList.setItemChecked(position, true);

        mDrawerLayout.closeDrawer(mDrawerList);
    }


    /**
     * Method for changing ActionBar title
     *
     * @param title CharSequence that represents new
     */
    @Override
    public void setTitle(CharSequence title) {

        getSupportActionBar().setTitle(title);
    }

    class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {

            NavigationDrawerItem drawerItem = (NavigationDrawerItem) parent.getItemAtPosition(position);

            setDisplayFragment(position, drawerItem.getLabel());
        }
    }

}
