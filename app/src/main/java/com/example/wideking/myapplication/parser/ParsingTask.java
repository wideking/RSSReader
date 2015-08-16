package com.example.wideking.myapplication.parser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.util.Log;

import com.example.wideking.myapplication.Helper.DataHelper;
import com.example.wideking.myapplication.feed.FeedItem;
import com.example.wideking.myapplication.news.News;
import com.example.wideking.myapplication.sqlTables.SQLTableFeedSites;
import com.example.wideking.myapplication.sqlTables.SQLTableNews;

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
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that creates Runnable task.Task parses information from URL and saves them as FeedItem object.Task has CountDownLatch Object for synchronisation between multiple Runnable objects.
 */
public class ParsingTask implements Runnable {
    final int THUMBNAIL = 170;
    public boolean isTest = false;
    Context context;
    FeedItem feed;
    CountDownLatch countDownLatch;
    Boolean flag_listExists = false;
    String pic_path;
    ArrayList<News> parsedNews;
    Boolean[] testFlags;


    /**
     * Contructor for ParsingTask. Constructor sets needed parameters for parsing.
     *
     * @param context        provide application context.
     * @param feed           FeedItem type parameter that contains information needed by the parser.(URL, name, etc...).
     * @param countDownLatch CountDownLatch object that enables synchronisation between multiple tasks.Used for letting Executor know that this task is finished.
     */
    ParsingTask(Context context, FeedItem feed, CountDownLatch countDownLatch) {
        this.context = context;
        this.feed = feed;
        this.countDownLatch = countDownLatch;
        pic_path = context.getFilesDir() + "/Images";
        parsedNews = new ArrayList<>();

    }

    /**
     * Method that creates new thread for doing the needed work.
     */

    @Override
    public void run() {
        long id = Thread.currentThread().getId();
        Log.d("TAG_XMLParsing", "Parsing started on thread : " + id);

        News news = new News();
        ArrayList<News> newsList = DataHelper.getNewsListByFeed(context, feed.getFeedName());//getting feed from DB


        News firstNews = new News();
        if (!newsList.isEmpty()) {//Checking if feed entry exists inside DB;

            firstNews = newsList.get(0);
            flag_listExists = true;
            Log.d("TAG_FirstNews", firstNews.getTime().toString());

        }

        try {//Connecting to feed and parsing
            Log.d("XMLParsing", "FeedURL " + feed.getFeedURL());
            URL u = new URL(feed.getFeedURL());
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


            parseURL(news, parsedNews, firstNews, XmlParser, eventType, flag_new_item, flag_news_exist);
            connection.disconnect();


        } catch (XmlPullParserException e) {
            DataHelper.setParserExceptionFlag(true, "Given url is not valid RSS feed source!");
            e.printStackTrace();
            Log.e("TAG", "Something went wrong with creating XML parser");
        } catch (MalformedURLException e) {
            DataHelper.setURLExceptionFlag(true, feed.getFeedURL());

            e.printStackTrace();
            Log.e("TAG", "URL is not valid");

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("TAG", "Problem with opening connection");
        } catch (NullPointerException e) {

            e.printStackTrace();
            Log.e("TAG", "Result was empty string");


        }


        testFeedAndUpdateDB(parsedNews);
        Log.d("TAG_XMLParser", "Parsing finished on thread :" + id);


    }

    /**
     * Method that saves parsed News as  ArrayList<News> to database.
     *
     * @param parsedNews type ArrayList object that contains new news that will be saved in the proper table.
     */
    protected void testFeedAndUpdateDB(ArrayList<News> parsedNews) {

        feed.setUpdateTime(new Date());
        if (isTest) {//if isTest flag is true, return arrayList newses

            testFlags = isParsingSuccessful();

            for (int i = 0; i < testFlags.length; i++) {
                Log.d("TAG_XMLflags_" + feed.getFeedName(), Boolean.toString(testFlags[i]));
            }
            //DataHelper.setTestFlags(testFlags);


        } else {


            if (flag_listExists) //If entry exist get News array, insert new news and put it back in array
            {
                dbStoreNews(parsedNews);


                flag_listExists = false;

            } else {//Store feed and news in DB and refresh drawer.
                dbStoreFeedAndNews(feed, parsedNews);
                ArrayList<String> categories = DataHelper.getCategoriesFromFeed(context);


            }

            countDownLatch.countDown();


        }
    }

    /**
     * Method for storing news intro database's table.
     *
     * @param news ArrayList<News> type object that will be stored into DB.
     */
    private void dbStoreNews(ArrayList<News> news)//Method for storing only news.
    {

        SQLTableNews.addNews(news, context, DataHelper.parseTableName(feed.getFeedURL()));
    }

    /**
     * Method for storing new Feed and News into DB.
     *
     * @param feed ArrayList<FeedItem> type object that will be stored into DB.
     * @param news ArrayList<News> type object that will be stored into DB.
     */
    private void dbStoreFeedAndNews(FeedItem feed, ArrayList<News> news)//Method for storing news and Feed.
    {

        SQLTableFeedSites.addSite(feed, context);
        SQLTableNews.addNews(news, context, DataHelper.parseTableName(feed.getFeedURL()));
    }

    /**
     * Method for parsing XML.
     *
     * @param news          current news object that is being created by parsing data.
     * @param parsedNews    ArrayList of all news object that are newer than last news in db.
     * @param firstNews     latest news in db for given news feed.
     * @param xmlParser     xmlParser object.
     * @param eventType     Tag in XML  that is currently being parsed.
     * @param flagNewItem   boolean flag that indicates if this is the new news item.
     * @param flagNewsExist boolean flag that indicates if this is the current news item exist.
     * @throws XmlPullParserException exception that is thrown if parser fails to parse given element.
     * @throws IOException            exception that is thrown if image can't be saved.
     */
    private void parseURL(News news, ArrayList<News> parsedNews, News firstNews, XmlPullParser xmlParser, int eventType, boolean flagNewItem, boolean flagNewsExist) throws XmlPullParserException, IOException {
        while (eventType != XmlPullParser.END_DOCUMENT) {

            switch (eventType) {

                case XmlPullParser.START_TAG:


                    String name = xmlParser.getName();


                    if (name.equals("item")) { //Code for recognising news. If it returns true, start getting news tags
                        news = new News();
                        flagNewItem = true;


                    }
                    flagNewsExist = setNewNews(news, firstNews, xmlParser, flagNewItem, flagNewsExist, name);


                    break;

                case XmlPullParser.END_TAG:
                    name = xmlParser.getName();


                    if (name.equals("item")) {
                        if (flagNewsExist == false) {//If  item is done and news doesn't exit add it to list & reset flags;
                            news.setFeedName(feed.getFeedName());
                            parsedNews.add(news);
                        }
                        flagNewsExist = false;
                        flagNewItem = false;


                    }
                    break;


            }
            eventType = xmlParser.next();


        }
    }

    /**
     * @param news          current news object that is being created by parsing data.
     * @param firstNews     latest news in db for given news feed.
     * @param xmlParser     xmlParser object.
     * @param flagNewItem   boolean flag that indicates if this is the new news item.
     * @param flagNewsExist boolean flag that indicates if this is the current news item exist.
     * @param name          XML tag's name.
     * @return returns  value of flagNewsExist.
     * @throws XmlPullParserException exception that is thrown if parser fails to parse given element.
     * @throws IOException            exception that is thrown if image can't be saved.
     */
    private boolean setNewNews(News news, News firstNews, XmlPullParser xmlParser, boolean flagNewItem, boolean flagNewsExist, String name) throws XmlPullParserException, IOException {
        if (flagNewItem) {

            if (name.equals("title") && !flagNewsExist) {
                //Log.d("TAG_Title",XmlParser.nextText());
                String title = xmlParser.nextText();

                news.setTitle(title);

            }
            if (name.equals("category") && !flagNewsExist) {


                //Log.d("TAG_Category",XmlParser.nextText());

                news.setCategory(xmlParser.nextText());

            }

            if (name.equals("description") && !flagNewsExist) {

                String description = xmlParser.nextText();
                //Log.d("TAG_Description_Image",description);
                String imageSource = getMatch("<\\s*img\\s*[^>]+src\\s*=\\s*(['\"]?)(.*?)\\1", description, 2); //getting images from Index.hr"
                if (imageSource != null) {
                    getPicture(imageSource, news);
                    description = description.replaceAll("\\<.*>", "");
                }


                news.setDescription(description);

            }


            if (name.equals("enclosure") && !flagNewsExist) {//getting images from Bug.hr
                //Log.d("TAG_image", XmlParser.nextText());
                String picture_url = xmlParser.getAttributeValue(null, "url");
                //Log.d("TAG_image", picture_url);
                getPicture(picture_url, news);


            }
            if (name.equals("link") && !flagNewsExist) {
                //Log.d("TAG_link", XmlParser.nextText());

                news.setLink_url(xmlParser.nextText());
            }
            if (name.equals("pubDate"))//getting publish date
            {
                flagNewsExist = parseDate(news, firstNews, xmlParser, flagNewsExist);


            }
        }
        Log.d("TAG_parserNewsExist", news.getTitle() + "news exist " + flagNewsExist);
        return flagNewsExist;
    }

    /**
     * * @param news          current news object that is being created by parsing data.
     *
     * @param firstNews     latest news in db for given news feed.
     * @param xmlParser     xmlParser object.
     * @param flagNewsExist boolean flag that indicates if this is the current news item exist.
     * @return returns  value of flagNewsExist.
     * @throws XmlPullParserException exception that is thrown if parser fails to parse given element.
     * @throws IOException            exception that is thrown if image can't be saved.
     */

    private boolean parseDate(News news, News firstNews, XmlPullParser xmlParser, boolean flagNewsExist) throws XmlPullParserException, IOException {
        String string_date = xmlParser.nextText();
        SimpleDateFormat xmlDateFormat = new SimpleDateFormat("EEE, d MMM yyyy H:m:s z");


        Date dateFromXML;

        try {

            dateFromXML = xmlDateFormat.parse(string_date);


            if (flag_listExists) {//if list entry exist, check news date; if news has older date than latest news in array, set flag to true, and ignore news.
                // Although pubDate tag comes almost at the end of item tag I think this is the efficient way of parsing data
                // than checking if name of news exist inside of array.
                if (dateFromXML.before(firstNews.getTime()) || dateFromXML.equals(firstNews.getTime())) {

                    flagNewsExist = true;

                }
                if (!flagNewsExist) {
                    news.setTime(dateFromXML);
                }


            } else if (!flagNewsExist)
                news.setTime(dateFromXML);


        } catch (ParseException e) {

            Log.d("TAG_timeException", "Problem with converting xmlDateFormat");
            e.printStackTrace();
        }
        return flagNewsExist;
    }

    /**
     * Method for retriving pictures from internet.
     *
     * @param pictureURL String trype parameter that should be URL.
     * @param news       current news item that is being parsed.
     * @throws IOException throws IOException if picture couldn't be written.
     */
    private void getPicture(String pictureURL, News news) throws IOException { //Function for getting images from web,saving them inside app dir and creating thumbnails.
        Log.d("TAG_image_getPicture", pictureURL);

        FileOutputStream fOutStream;
        Bitmap pic;
        Bitmap pic_thumbnail;
        String title = news.getTitle();
        try {
            URL u = new URL(pictureURL);
            HttpURLConnection connection =
                    (HttpURLConnection) u.openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream input = connection.getInputStream();
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


            news.setPicture_url(Uri.fromFile(picture_thumbnail));

            fOutStream.flush();
            fOutStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("TAG", "URL is not valid");
        }


    }

    /**
     * Method for getting unparsable img tags. It extracts img values like image url.
     *
     * @param patternString
     * @param text
     * @param groupIndex
     * @return
     */

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

    /**
     * Method for testing successfulness of parsing.It will return boolean array with values of tests.
     *
     * @return boolean array. Position 0 is category. Position 1 is title. Position 2 is description. Position 3 is picture url. Position 4 is news link. Position 5 is feed name.
     */
    public Boolean[] isParsingSuccessful() {
        isTest = true;

        Boolean[] isNewsRead = {true, true, true, true, true, true};

        for (News news : parsedNews) {//Go thought news and check if all fields have new values. If some field doesn't have new value change flag of that field.
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
}
