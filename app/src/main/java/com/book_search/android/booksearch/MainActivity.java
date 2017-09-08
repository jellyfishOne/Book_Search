package com.book_search.android.booksearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    //Tag
    public static final String MAIN_TAG = "com.bookSearch.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find the search button
        Button search = (Button) findViewById(R.id.search_button);
        //set a click listener on the Button
        search.setOnClickListener(new View.OnClickListener(){
            //This code in the method will be executed when the search button is clicked
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BookResults.class);
                EditText input = (EditText) findViewById(R.id.input_edit_text);
                String inputString = input.getText().toString();

                //Add the EdtText's value to the intent
                intent.putExtra(MAIN_TAG, inputString);
                startActivity(intent);
            }
        });
    }
}
