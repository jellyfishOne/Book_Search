package com.book_search.android.booksearch;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * A BookAdapter knows how to create a list item layout for each book in te
 * data source (a list of Book objects).
 *
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user
 */

public class BookAdapter extends ArrayAdapter<Book>{

    /**
     * Constructs a new BookAdapter
     * @param context of the app
     * @param books is the list of books, which is the data source of the adapter
     */
    public BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }

    /**
     * Returns a list item view that displays information about the book
     * in the list of books
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Check if there is an existing list item view (called converView) that
        //we can reuse, otherwise, if converView is null, then inflate a new list item layout
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list_item, parent, false);
        }

        //Find the book at the given position in the list of books
        Book currentBook = getItem(position);

        //Find the ImageView with view ID thumbnail_view
        ImageView thumbNailView = (ImageView) listItemView.findViewById((R.id.thumbnail_view));
        //Display the thumbnail for the current book
        thumbNailView.setImageBitmap(currentBook.getThumbnailURL());

        //Find the TextView with the view ID title_view
        TextView titleView = (TextView) listItemView.findViewById((R.id.title_view));
        //Display the title for the book
        titleView.setText(currentBook.getTitle());

        //Find the TextView with the view ID author_view
        TextView authorView = (TextView) listItemView.findViewById((R.id.author_view));
        //Display the author of the book
        authorView.setText(currentBook.getAuthor());

        //Find the TextView with the view ID published_view
        TextView publishedView = (TextView) listItemView.findViewById((R.id.publish_view));
        //Display the published date of the book
        publishedView.setText(currentBook.getPublishedYear());

        //Find the TextView with the view ID rating_view
        TextView ratingView = (TextView) listItemView.findViewById((R.id.rating_view));

        //TODO fix the formatter
        //Check if there is a rating for the current book
        if(!currentBook.getRating().equals("N/A")) {
            //Convert String to a double and set the corresponding color
            double formattedRating = formatRating(currentBook.getRating());

            //Set the proper background color on the rating circle.
            //Fetch the background from the TextView, which is ratingView
            GradientDrawable ratingCircle = (GradientDrawable) ratingView.getBackground();
            //Get the appropriate background color based on the current book rating
            int magnitudeColor = getRatingColor(formattedRating);
            ratingCircle.setColor(magnitudeColor);

            //Display rating for the book
            ratingView.setText(currentBook.getRating());
        } else{
            //Otherwise, Display the "N/A" rating for the book
            ratingView.setText(currentBook.getRating());
            //Set the proper background color on the rating circle.
            //Fetch the background from the TextView, which is ratingView
            GradientDrawable ratingCircle = (GradientDrawable) ratingView.getBackground();
            //Get the appropriate background color based on the current book rating
            int magnitudeColor = ContextCompat.getColor(getContext(), R.color.rating1);
            ratingCircle.setColor(magnitudeColor);

        }


        //Return the list item view
        return listItemView;

    }

    /**
     * Converts the String rating for the book into a double
     */
    private double formatRating(String rating){
        double ratingDouble = Double.parseDouble(rating);
        return ratingDouble;

    }

    /**
     * Returns the color corresponding to the decimal magnitude value
     */
    private int getRatingColor(double rating){
        int ratingColorResourceId;
        int magnitudeFloor = (int) Math.floor(rating);
        switch(magnitudeFloor){
            case 0:
            case 1:
                ratingColorResourceId = R.color.rating1;
                break;
            case 2:
                ratingColorResourceId = R.color.rating2;
                break;
            case 3:
                ratingColorResourceId = R.color.rating3;
                break;
            case 4:
            case 5:
                ratingColorResourceId = R.color.rating4;
                break;
            default:
                ratingColorResourceId = R.color.rating1;
                break;
        }
        return ContextCompat.getColor(getContext(), ratingColorResourceId);
    }
}

