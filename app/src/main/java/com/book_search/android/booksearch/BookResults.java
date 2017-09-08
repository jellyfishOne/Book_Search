package com.book_search.android.booksearch;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 *Displays book results
 */

public class BookResults extends AppCompatActivity implements LoaderCallbacks<List<Book>>{

    //Tag for log messages
    private static final String LOG_TAG = BookResults.class.getName();

    //TextView that is displayed when the list is empty
    private TextView mEmptyStateTextView;

    //String for URL to search
    private String searchURl;

    //Amount of search results
    private static final String NUMBER_OF_SEARCH_RESULTS = "&maxResults=30";

    //URL for book data from Google API
    private static final String GOOGLE_BOOK_URL =
            "https://www.googleapis.com/books/v1/volumes?q=";

    //Adapter for the list of books
    private BookAdapter mAdapter;

    //Constant value for the book loader ID
    private static final int BOOK_LOADER_ID = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_results_activity);

        //Get the intent that started this activity and extract the string
        Intent intent = getIntent();
        String inputString = intent.getStringExtra(MainActivity.MAIN_TAG);
        getSearchURl(inputString);

        //Find a reference to the ListView in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);

        //Create a new adapter that takes an empty list of books as imput
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        //Create a new empty textView
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        //Set empty TextView
        bookListView.setEmptyView(mEmptyStateTextView);

        //Set the adapter on the ListView
        //So the list can be populated in the user interface
        bookListView.setAdapter(mAdapter);

        //Set an item click listener on the ListView, which sends an intent
        //to open a website with more information about the selected book
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Find the current book that was clicked on
                Book currentBook = mAdapter.getItem(position);

                //Convert the String webisteURL into a URI object (to pass inot the Internet constructor)
                Uri bookUri = Uri.parse(currentBook.getWebsiteURL());

                //Create a new intent to view the book URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                //Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        //Get a reference to the ConnectivityManager to check of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        //Get details on currently activity default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        //If there is a network connection, fetch data
        if(networkInfo != null && networkInfo.isConnected()){
            //Get a reference to the LoaderManager, in order to interact with loaders
            LoaderManager loaderManager = getLoaderManager();

            //Initialize the loader. Pass in the int ID constant defined above and pass in null
            //for the bundle. Pass in this activity for the Loadercallbacks parameter (which is valid
            //because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        } else {

            //Otherwise, display error
            //First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            //Update empty state with noe connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle){
        //Create a new loader for the given URL
        return new BookLoader(this, searchURl);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books){
        //Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        //Set empty state text to display "No books found."
        mEmptyStateTextView.setText(R.string.no_books);

        //Clear the dapter of previous book data
        mAdapter.clear();

        //If there is a valid list of Book objects, then add them to the adapter's
        //data set. This will trigger the ListView to update.
        if(books != null && !books.isEmpty()){
            mAdapter.addAll(books);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader){
        //Loader reset, so we can clear out our existing data
        mAdapter.clear();
    }

    /**
     * Create a search query based on the user's input
     */
    private void getSearchURl(String query){
        String formattedInput = null;
        try {
            formattedInput = URLEncoder.encode(query, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(LOG_TAG, "Error trying to encode input. ", e);
        }
        searchURl = GOOGLE_BOOK_URL + formattedInput + NUMBER_OF_SEARCH_RESULTS;
    }

}
