# Book_Search
An Android application that lets you search Google books for any book title and returns 30 results.
You can click each result to get more information about the book.

For this application, I used a LoaderManager to make an Http Get request to the Google books API.
I parsed the JSON response and created a custom ArrayAdapter to display the search results.
I used an onItemClickListener that launches an intent that opens a webpage with more information on the book. 

The application was built on Android Studio, Android SDK v26, and Android Build Tools 26.0.0
 

