package com.book_search.android.booksearch;

import android.graphics.Bitmap;

/**
 * A Book object contains information related to a single book
 */

public class Book {

    // Thumbnail url for the book
    private Bitmap mThumbnailURL;

    // Title of the book
    private String mTitle;

    // Author of the book
    private String mAuthor;

    // Year the book was published
    private String mPublished;

    // Rating of the book
    private String mRating;

    // Website URL for the book
    private String mWebsiteURL;

    /**
     * Constructs a new Book object
     *
     * @param published is the year the book was published
     * @param title is the title of the book
     * @param author is the name of the author of the book
     * @param thumbnailURL is the URL of the book's thumbanil
     * @param rating is the rating for the book
     * @param websiteURL is the URL for the book's website
     */
    public Book(String title, String author, String published, Bitmap thumbnailURL,
                String rating, String websiteURL){
        mTitle =title;
        mAuthor = author;
        mThumbnailURL = thumbnailURL;
        mRating = rating;
        mWebsiteURL =websiteURL;
        mPublished =published;
    }

    /**
     * Return the year the book was published
     */
    public String getPublishedYear(){
        return mPublished;
    }
    /**
     * Returns the title of the book
     */
    public String getTitle(){
        return mTitle;
    }

    /**
     * Returns the author of the book
     */
    public String getAuthor(){
        return mAuthor;
    }

    /**
     * Returns the thumbnail URL for the book
     */
    public Bitmap getThumbnailURL(){
        return mThumbnailURL;
    }
    
    /**
     * Returns the rating for the book
     */
    public String getRating(){
        return mRating;
    }

    /**
     * Returns the website URL for the book
     */
    public String getWebsiteURL(){
        return mWebsiteURL;
    }


}
