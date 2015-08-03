package com.example.wideking.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by WideKing on 2.1.2015..
 */
public class XMLParsing {

    final int THUMBNAIL = 170;
    public boolean isTest = false;
    ArrayList<FeedItem> feedItem = new ArrayList<>();
    MainActivity mainActivity;
    FeedItem feed;
    String pic_path;

    GetXml getXmlObject;
    ArrayList<News> newsList;


    public XMLParsing(MainActivity mainActivity, FeedItem feed) {
        this.mainActivity = mainActivity;
        this.feed = feed;

        pic_path = mainActivity.getApplicationContext().getFilesDir() + "/Images";
    }

    public void Refresh() {

        Date currentTime = new Date();
        String updateInterval = mainActivity.getResources().getStringArray(R.array.Update_interval)[0];//This should return first item inside the array.
        Log.d("TAG_XML_UpdateIntStr", updateInterval);
        int interval = Integer.parseInt(SQLTableUserSettings.getSettingsValue(updateInterval, mainActivity.getApplicationContext()));//Read update interval from DB.
        Log.d("TAG_settingsUpdate", "Update interval is:" + updateInterval);
        if (currentTime.getTime() - feed.getUpdateTime().getTime() >= interval * 60 * 1000) {
            Log.d("TAG_XML", "Enough time has passed since last update");
            getXmlObject = new GetXml();
            getXmlObject.execute(feed.getFeedURL());
        }

    }

    public boolean[] isParsingSuccessful() {
        isTest = true;

        boolean[] isNewsRead = {true, true, true, true, true, true};

        for (News news : newsList) {//Go thought news and check if all fields have new values. If some field doesn't have new value change flag of that field.
            if ("Empty".equals(news.getCategory())) {
                isNewsRead[0] = false;

            }
            if ("Empty".equals(news.getTitle())) {
                isNewsRead[1] = false;

            }
            if ("Empty".equals(news.getDescription())) {
                isNewsRead[2] = false;
            }
            if (Uri.EMPTY.equals(news.getPicture_url())) {
                isNewsRead[3] = false;

            }
            if ("Empty".equals(news.getLink_url())) {
                isNewsRead[4] = false;
            }
            if ("Empty".equals(news.getFeedName())) {
                isNewsRead[5] = false;

            }


        }
        return isNewsRead;

    }


    private class GetXml extends AsyncTask<String, Integer, ArrayList<News>> {
        Boolean flag_listExists = false;
        Integer index;


        @Override
        protected void onPreExecute() { //Setting up UI

            mainActivity.progressBar.setVisibility(View.VISIBLE);
            mainActivity.progressText.setVisibility(View.VISIBLE);
            //mainActivity.lv_layout.setVisibility(View.GONE);


            super.onPreExecute();
        }

        @Override
        protected ArrayList<News> doInBackground(String... params) {
            Log.d("TAG_XMLParsingParam", params[0]);
            News n = new News();
            ArrayList<News> newsList = DataHelperClass.getNewsListByFeed(mainActivity.getApplicationContext(), feed.getFeedName());//getting feed from DB
            ArrayList<News> parsedNews = new ArrayList<>();

            News firstNews = new News();
            if (!newsList.isEmpty()) {//Checking if feed entry exists inside DB;


                firstNews = newsList.get(0);
                flag_listExists = true;
                Log.d("TAG_FirstNews", firstNews.getTime().toString());

            }

            try {//Connecting to feed and parsing
                URL u = new URL(params[0]);
                HttpURLConnection connection =
                        (HttpURLConnection) u.openConnection();
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(10000);
                connection.setRequestMethod("GET");
                connection.connect();
                InputStream in = connection.getInputStream();
                XmlPullParserFactory XmlFactory = XmlPullParserFactory.newInstance();

                XmlPullParser XmlParser = XmlFactory.newPullParser();
                XmlParser.setInput(in, null);
                int eventType = XmlParser.getEventType();
                boolean flag_new_item = false;
                boolean flag_news_exist = false;


                while (eventType != XmlPullParser.END_DOCUMENT) {

                    switch (eventType) {

                        case XmlPullParser.START_TAG:


                            String name = XmlParser.getName();


                            if (name.equals("item")) { //Code for recognising news. If it returns true, start getting news tags
                                n = new News();
                                flag_new_item = true;


                            }
                            if (flag_new_item) {

                                if (name.equals("title") && !flag_news_exist) {
                                    //Log.d("TAG_Title",XmlParser.nextText());
                                    String title = XmlParser.nextText();

                                    n.setTitle(title);

                                }
                                if (name.equals("category") && !flag_news_exist) {


                                    //Log.d("TAG_Category",XmlParser.nextText());

                                    n.setCategory(XmlParser.nextText());

                                }

                                if (name.equals("description") && !flag_news_exist) {

                                    String description = XmlParser.nextText();
                                    //Log.d("TAG_Description_Image",description);
                                    String imageSource = getMatch("<\\s*img\\s*[^>]+src\\s*=\\s*(['\"]?)(.*?)\\1", description, 2); //getting images from Index.hr"
                                    if (imageSource != null) {
                                        getPicture(imageSource, n);
                                        description = description.replaceAll("\\<.*>", "");
                                    }


                                    n.setDescription(description);

                                }


                                if (name.equals("enclosure") && !flag_news_exist) {//getting images from Bug.hr
                                    //Log.d("TAG_image", XmlParser.nextText());
                                    String picture_url = XmlParser.getAttributeValue(null, "url");
                                    //Log.d("TAG_image", picture_url);
                                    getPicture(picture_url, n);


                                }
                                if (name.equals("link") && !flag_news_exist) {
                                    //Log.d("TAG_link", XmlParser.nextText());

                                    n.setLink_url(XmlParser.nextText());
                                }
                                if (name.equals("pubDate"))//getting publish date
                                {
                                    String string_date = XmlParser.nextText();
                                    SimpleDateFormat xmlDateFormat = new SimpleDateFormat("EEE, d MMM yyyy H:m:s z");


                                    Date dateFromXML;

                                    try {

                                        dateFromXML = xmlDateFormat.parse(string_date);


                                        if (flag_listExists) {//if list entry exist, check news date; if news has older date than latest news in array, set flag to true, and ignore news.
                                            // Although pubDate tag comes almost at the end of item tag I think this is the efficient way of parsing data
                                            // than checking if name of news exist inside of array.
                                            if (dateFromXML.before(firstNews.getTime()) || dateFromXML.equals(firstNews.getTime())) {

                                                flag_news_exist = true;

                                            }
                                            if (!flag_news_exist)
                                                n.setTime(dateFromXML);


                                        } else if (!flag_news_exist)
                                            n.setTime(dateFromXML);


                                    } catch (ParseException e) {

                                        Log.d("TAG_timeException", "Problem with converting xmlDateFormat");
                                        e.printStackTrace();
                                    }


                                }
                            }


                            break;

                        case XmlPullParser.END_TAG:
                            name = XmlParser.getName();


                            if (name.equals("item")) {
                                if (flag_news_exist == false) {//If  item is done and news doesn't exit add it to list & reset flags;
                                    n.setFeedName(feed.getFeedName().replace("_", " "));
                                    parsedNews.add(n);
                                }
                                flag_news_exist = false;
                                flag_new_item = false;


                            }
                            break;


                    }
                    eventType = XmlParser.next();


                }
                connection.disconnect();


            } catch (XmlPullParserException e) {
                e.printStackTrace();
                Log.d("TAG", "Something went wrong with creating XML parser");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("TAG", "URL is not valid");

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("TAG", "Problem with opening connection");
            } catch (NullPointerException e) {

                e.printStackTrace();
                Log.d("TAG", "Result was empty string");


            }

            return parsedNews;
        }

        private void getPicture(String pic_url, News n) throws IOException { //Function for getting images from web,saving them inside app dir and creating thumbnails.
            Log.d("TAG_image_getPicture", pic_url);

            FileOutputStream fOutStream;
            Bitmap pic;
            Bitmap pic_thumbnail;
            String title = n.getTitle();

            InputStream input = new URL(pic_url).openStream();

            title = title.replaceAll(" ", "_");
            File picture = new File(pic_path, "IMG_" + title + ".png");

            fOutStream = new FileOutputStream(picture);
            BitmapFactory.Options Options = new BitmapFactory.Options();


            Options.inMutable = true;
            Options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            pic = BitmapFactory.decodeStream(input, null, Options);
            pic.compress(Bitmap.CompressFormat.PNG, 100, fOutStream);
            pic_thumbnail = ThumbnailUtils.extractThumbnail(pic, THUMBNAIL, THUMBNAIL);


            input.close();


            fOutStream.flush();
            fOutStream.close();


            File picture_thumbnail = new File(pic_path, "IMG_" + title + "_thumbnail" + ".png");
            fOutStream = new FileOutputStream(picture_thumbnail);

            pic_thumbnail.compress(Bitmap.CompressFormat.PNG, 100, fOutStream);


            n.setPicture_url(Uri.fromFile(picture_thumbnail));

            fOutStream.flush();
            fOutStream.close();

        }

        public String getMatch(String patternString, String text, int groupIndex) {
            // /Because some xml have imgs inside unparsable tags patters are used to extract img tag.
            // Firstly string pattern is converted to pattern and then other getMatch is called which returns string that is result of pattern operation on given string.

            Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            return getMatch(pattern, text, groupIndex);
        }

        public String getMatch(Pattern pattern, String text, int groupIndex) {
            if (text != null) {
                Matcher matcher = pattern.matcher(text);
                String match = null;
                while (matcher.find()) {
                    match = matcher.group(groupIndex);
                    break;
                }
                return match;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<News> newses) {
            feed.setUpdateTime(new Date());
            if (isTest) {//if isTest flag is true, return arrayList newses
                newsList = newses;
                boolean[] flags = isParsingSuccessful();
                for (int i = 0; i < flags.length; i++) {
                    Log.d("TAG_XMLflags_" + feed.getFeedName(), Boolean.toString(flags[i]));
                }


            } else {
                //Some UI things
                mainActivity.progressBar.setVisibility(View.GONE);
                mainActivity.progressText.setVisibility(View.GONE);
                mainActivity.lv_layout.setVisibility(View.VISIBLE);


                if (flag_listExists) //If entry exist get News array, insert new news and put it back in array
                {
                    dbStoreNews(newses);


                    flag_listExists = false;

                } else {//Store feed and news in DB and refresh drawer.
                    dbStoreFeedAndNews(feed, newses);
                    ArrayList<String> categories = DataHelperClass.getCategoriesFromFeed(mainActivity.getApplicationContext());

                    mainActivity.inicializeDrawer(categories);

                }


                //Log.d("TAG_onPostExecute", "onPostExecute");


            }


        }


        private void dbStoreNews(ArrayList<News> news)//Method for storing only news.
        {

            SQLTableNews.addNews(news, mainActivity.getApplicationContext(), DataHelperClass.parseTableName(feed.getFeedURL()));
        }

        private void dbStoreFeedAndNews(FeedItem feed, ArrayList<News> news)//Method for storing news and Feed.
        {

            SQLTableFeedSites.addSite(feed, mainActivity.getApplicationContext());
            SQLTableNews.addNews(news, mainActivity.getApplicationContext(), DataHelperClass.parseTableName(feed.getFeedURL()));
        }


        @Override
        protected void onProgressUpdate(Integer... values) {

            super.onProgressUpdate(values);
        }
    }


}

