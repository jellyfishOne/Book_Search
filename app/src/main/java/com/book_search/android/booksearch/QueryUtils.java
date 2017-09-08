package com.book_search.android.booksearch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving book data from Google books
 */

public class QueryUtils {
    /** Tag for the log messages */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should create QueryUtils object.
     * This class is only meant to hold static variables and methods, which can be
     * accessed directly from the class name QueryUtils.
     */
    private QueryUtils(){
    }

    /**
     * Query Google maps and returns a list of Book objects
     */
    public static List<Book> fetchBookData(String requestURL){
        //calls the createURL method to create a URL object
        URL url = createURL(requestURL);

        //Perform HTTP request to the URL and receive a JSON response
        String jsonReponse = null;
        try{
            //calls the makeHttpRequest method and returns a JSON string
            jsonReponse = makeHttpRequest(url);
        } catch (IOException e){
            Log.e(LOG_TAG,"Problem making HTTP request: ", e);
        }

        //Calls extractFeatureFromJson method to extract relevant fields
        //from the JSON response and create a list Book objects
        List<Book> books = extractFeatureFromJson(jsonReponse);

        //Return the list of Books
        return books;
    }

    /**
     * Returns a new URL object from the given string URL.
     */
    private static URL createURL(String stringURL){
        URL url = null;
        try{
            url = new URL(stringURL);
        } catch (MalformedURLException e) {
           Log.e(LOG_TAG, "Problem building the URL: ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String
     */
    private static String makeHttpRequest(URL url) throws IOException{
        String jsonResponse = "";

        //If the URL is null, then return early
        if(url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream =null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds*/);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //If the request was successful (response code 200)
            //then read the input stream and parse the reponse
            if(urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e){
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream != null){
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies that an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Make an HTTP request to the thumbnail URL and return a BitMap as the response
     */
    private static Bitmap getThumbnailImage(String url) throws IOException{
        Bitmap bitMap = null;

        //If the URL is null, then return early.
        if(url == null){
            return bitMap;
        }

        try {
            InputStream in = new java.net.URL(url).openStream();
            bitMap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        return bitMap;

    }

    /**
     * Convert the InputStream into a String which contains the whole JSON response
     * from the server
     */
    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of Book objects that has been built up from parsing the given
     * JSON response
     */
    private static List<Book> extractFeatureFromJson(String bookJSON){
        //If the JSON string is empty or null, then return early
        if(TextUtils.isEmpty(bookJSON)){
            return null;
        }

        //Create an empty ArrayList that we can add books to
        List<Book> books = new ArrayList<>();

        //Try to parse the JSON reponse String. If there's a problem with the way
        // th JSON is formatted, a JSONException exception object will be thrown.
        //Cath the exception so the app doesn't crash, and log the error
        try{
            //Create a JSONObject from the JSON reponse string
            JSONObject baseJsonResponse = new JSONObject(bookJSON);

            //Extract the JSONArray associated with the key called "items",
            //which represents a list of items (or books).
            JSONArray bookArray = baseJsonResponse.getJSONArray("items");

            //For each book in the bookArray, create a Book object
            for(int i = 0; i < bookArray.length(); i++){
                //Get a single book at position i within the list of book items
                JSONObject currentBook = bookArray.getJSONObject(i);

                //Create a JSONArray that will hold an array of authors for the book
                JSONArray  authorArray;

                //Create a String to hold authors
                String author = "";

                //Get the value for the key "volumeInfo"
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");
                //Extract the value for the key called "authors"
                if(volumeInfo.has("authors")){
                    authorArray = volumeInfo.getJSONArray("authors");
                    //Extract the values from the "authors' array
                    for(int j = 0; j < authorArray.length(); j ++){
                        author += authorArray.getString(j) + "\n";
                    }
                } else {
                    author = " ";
                }

                //Extract the value for the key called "title" from the JSONObject volumeInfo
                String title = volumeInfo.getString("title");

                //Extract the value from the key called "published date"
                String publishedDate;

                if(volumeInfo.has("publishedDate")){
                    publishedDate = volumeInfo.getString("publishedDate");
                } else {
                    publishedDate = "";
                }

                String rating;
                if(volumeInfo.has("averageRating")){
                    //Extract the value from the key called "averageRating"
                    rating = volumeInfo.getString("averageRating");
                } else{
                    rating = "N/A";
                }

                //For a given book, extract the JSONObject associated with
                //the key called "imageLinks", which represents a list of all
                //thumbnail links for the book
                JSONObject imageLinks; 
                Bitmap thumbnail = null;

                if(volumeInfo.has("imageLinks")){
                    imageLinks = volumeInfo.getJSONObject("imageLinks");
                    //Extract the  value for the key called "thumbnail"
                    String thumbnailString = imageLinks.getString("thumbnail");
                    //Get the image from the "thumbnail" url by calling the getThumbnailImage() method.
                    try {
                        thumbnail = getThumbnailImage(thumbnailString);
                    }catch (IOException e){
                        Log.e(LOG_TAG, "Problem getting thumbnail image. ", e);

                    }
                }
                //Extract the  value for the key called "infoLink"
                String webUrl = volumeInfo.getString("infoLink");

                //Create a new Book object with the tite, author, published date,
                //rating, thumbnailURL, and websiteURL
                Book book = new Book(title, author, publishedDate, thumbnail,
                        rating, webUrl);

                //Add the new Book object to the list of books
                books.add(book);
            }

        }catch (JSONException e){
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the Book JSON results", e);
        }
        //Return the list of books
        return books;
    }
}
