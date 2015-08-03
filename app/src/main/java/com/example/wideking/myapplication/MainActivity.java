package com.example.wideking.myapplication;

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

import java.io.File;
import java.util.ArrayList;


//TODO Implement adding new sources with check if data is parseable and check if data exist.Tell user what is the problem.
//TODO check XMLParsing, impelemnt way procceed on this input. done
//TODO create Class for forking asyncTasks. http://developer.android.com/reference/android/os/AsyncTask.html#executeOnExecutor%28java.util.concurrent.Executor,%20Params...%29
//TODO when changing to landscape, load fragment that was displayed.
public class MainActivity extends ActionBarActivity {
    static String URL_BUG_HR = "http://www.bug.hr/rss/vijesti/";
    static String URL_INDEX_HR = "http://www.index.hr/najnovije/rss.ashx";
    static String URL_INDEX_HR_VIJESTI = "http://www.index.hr/vijesti/rss.ashx";

    ArrayList<FeedItem> feedItem = new ArrayList<>();
    ProgressBar progressBar;
    TextView progressText;
    LinearLayout lv_layout;


    DrawerLayout mDrawerLayout;
    ListView mDrawerList;

    XMLParsing parsingBugHr;
    XMLParsing parsingBugHr1;
    XMLParsing parsingIndexHr;
    XMLParsing parsingIndexHr2;
    XMLParsing parsingIndexHr3;


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

        feedItem = DataHelperClass.getSelectedFeed(getApplicationContext());
        if (feedItem.isEmpty())//If there are no default feeds or no db  parse this default feeds
        {


            ArrayList<String> categories = DataHelperClass.getCategoriesFromFeed(getApplicationContext());
            inicializeDrawer(categories);
            setDisplayFragment(0, "Empty");


            parsingIndexHr = new XMLParsing(this, DataHelperClass.getFeedByName("Index vijesti", "Politics", URL_INDEX_HR_VIJESTI, getApplicationContext()));
            parsingIndexHr.Refresh();
            parsingBugHr = new XMLParsing(this, DataHelperClass.getFeedByName("Bug vijesti", "Technology", URL_BUG_HR, getApplicationContext()));
            parsingBugHr.Refresh();
            parsingBugHr.isTest = true;

            parsingIndexHr2 = new XMLParsing(this, DataHelperClass.getFeedByName("Index najnovije", "Politics", URL_INDEX_HR, getApplicationContext()));
            parsingIndexHr2.Refresh();
            parsingIndexHr2.isTest = true;

            parsingBugHr1 = new XMLParsing(this, DataHelperClass.getFeedByName("Bug vijesti", "Technology", URL_BUG_HR, getApplicationContext()));
            parsingBugHr1.Refresh();

            parsingIndexHr3 = new XMLParsing(this, DataHelperClass.getFeedByName("Index najnovije", "Politics", URL_INDEX_HR, getApplicationContext()));
            parsingIndexHr3.Refresh();
            XMLParsing parsing24sataSport = new XMLParsing(this, DataHelperClass.getFeedByName("24 sata Sport", "Sport", "http://www.24sata.hr/feeds/sport.xml", getApplicationContext()));
            parsing24sataSport.Refresh();
            parsing24sataSport.isTest = true;
            XMLParsing parsing24sataNews = new XMLParsing(this, DataHelperClass.getFeedByName("24 sata Vijesti", "Politics", "http://www.24sata.hr/feeds/news.xml", getApplicationContext()));
            parsing24sataNews.Refresh();
            parsing24sataNews.isTest = true;

            XMLParsing parsing24sataSport1 = new XMLParsing(this, DataHelperClass.getFeedByName("24 sata Sport", "Sport", "http://www.24sata.hr/feeds/sport.xml", getApplicationContext()));
            parsing24sataSport1.Refresh();

            XMLParsing parsing24sataNews1 = new XMLParsing(this, DataHelperClass.getFeedByName("24 sata Vijesti", "Politics", "http://www.24sata.hr/feeds/news.xml", getApplicationContext()));
            parsing24sataNews1.Refresh();


        } else {

            getUpdateFromRSS();
            ArrayList<String> categories = DataHelperClass.getCategoriesFromFeed(getApplicationContext());
            inicializeDrawer(categories);
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
        for (FeedItem feed : feedItem) {
            Log.d("TAG_GetNewsFromRSS", feed.getFeedName());
            XMLParsing parser = new XMLParsing(MainActivity.this, DataHelperClass.getFeedByName(feed.getFeedName(), feed.getCategory(), feed.getFeedURL(), getApplicationContext()));

        }
    }


    public void inicializeDrawer(ArrayList<String> categories) {
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
        // Create a new fragment and specify the planet to show based on position

        // Highlight the selected item, update the title, and close the drawer

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
