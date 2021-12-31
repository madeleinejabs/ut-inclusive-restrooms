# UT Inclusive Restrooms
#### By Madeleine Jabs and Harrison Ward from Austin, Texas
#### Video Demo: (https://youtu.be/y_MOzNBWOzA)
#### Description:

When we were brainstorming final project ideas, we knew that we wanted to create a program that would help transgender and/or gender non-conforming people. We knew that gender-inclusive restrooms at our university, the University of Texas at Austin, are difficult to find, only showing up in certain buildings on campus and often tucked away within that building. The Gender and Sexuality Center at our school came up with one solution in the form of a [list of gender-neutral restrooms](https://diversity.utexas.edu/genderandsexuality/gender-inclusive-restrooms/), but from experience it is cumbersome to scroll through the entire list to find the nearest restroom, not to mention the fact that the webpage is only available if you have good internet access. Our solution was UT Inclusive Restrooms: an Android App that finds the nearest gender-inclusive restrooms at UT for you, using JSON data from the university list of gender-neutral restrooms.

We built our project in Android Studio using the Kotlin language. The basic architecture of our app involved fetching restroom data from either the remote source - the GSC database - or a local cache - a SQLite database built using the Room Persistence Library and funneling that data through a processing data layer in order to display it to the user in a UI layer at the front end of the app. 

Fetching remote data was implemented in the RestroomRemoteDataSource class's `getRestrooms()` method, which would first fetch the data as a JSON string from the back end of the University's ArcGIS webmap and then parse that string to put all important restroom data into the Restroom data class. The `getRestroooms()` method would then use a geocoder to store latitude and longitude information in the Restroom data class and return a mutable list of Restroom objects.

The local data source, implemented in the RestroomLocalDataSource class, also has a `getRestrooms()` method which returns a mutable list of Restroom objects, this time fetched from a local SQLite database on the user's phone. This was implemented using the Room persistence library, which requires 3 classes: an AppDatabase class from which to fetch a Restroom Data Access Object, a Data Access Object - implemented in RestroomDao - to query the database, and a data class to determine column information for each database element. So, in the `getRestrooms()` method we instantiate a database, instantiate a restroomDao from that database, and query the restroomDao get all the database information. The RestroomLocalDataSource class also has an `update()` method which clears the database and inserts a mutable list of restrooms into the database.

The RestroomRepository class determines which data source to use, updates the RestroomLocalDataSource as necessary, and processes the data in its `sortRestrooms(currentLocation)` function. If the RestroomLocalDataSource has no data or outdated data, this method will get the list of restrooms from the RestroomRemoteDataSource and update the RestroomLocalDataSource. Then, it will calculate distance between the user's currentLocation passed into the function and the location of each restroom, store that data in the Restroom data class, and sort the list of restrooms by distance. The RestroomRepository's `getRestrooms()` method simply returns an immutable list of restrooms if the restrooms have already been sorted. 

The RestroomViewModel class contains what is called the UI state, which holds all the data that the UI needs so that if the UI is refreshed or changes configuration the data isn't lost and is instead preserved. Therefore the RestroomViewModel class contains a StateFlow<List<Restroom>> attribute which determines the UI state. The RestroomViewModel also has an `updateUiState` method which calls the RestroomRepository's methods to get an updated list of Restroom objects.

The RestroomActivity class contains most of the information about what gets shown to the user. It asks the user for location permissions, sets the appropriate views with the right colors, and observes the UI state to constantly display the data correctly. It works with the RestroomAdapter to display the data in a RecyclerView, which recycles layout objects known as "frames" when the user scrolls to improve performance.

Other important files we used include XML files which determine the layout for what the user sees. The restroom_frame.xml file determines the layout for each RecyclerView "frame" that the user sees, while the activity_restroom.xml file contains the loading screen and the recycler view. Other XML files in the "values" folder contain data pertaining to colors, dimensions, strings, and themes so that we don't have to manually type out strings or dimensions whenever we reuse them.

Along the way we made a number of important design choices to improve app performance and intuitive design. For performance, we decided to implement local caching to allow for easy offline access (students may not always have wifi while walking about campus) and allow for faster loading times. We also decided to store latitude and longitude information within the Restroom objects because calls to determine restroom location with Geocoder were a noticeable bottleneck in our app's performance. In terms of making our UI helpful and easy to understand, we decided to add a Google Maps icon to open up directions in Google Maps and to use a RecyclerView to display all active restrooms rather than a limited number of restrooms. This way, the user has full access to the data and can make the most flexible decisions. In addition, in the beginning of our project we had an activity whose only function was to open up the Restroom Activity when the user tapped a button labelled "Find me a Restroom." We determined that this functionality was unnecessary as, especially for people who had been using the app for a while, tapping the button every time would quickly grow tiresome. 
