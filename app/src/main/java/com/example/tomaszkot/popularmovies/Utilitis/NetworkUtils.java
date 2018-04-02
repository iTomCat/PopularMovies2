package com.example.tomaszkot.popularmovies.Utilitis;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import com.example.tomaszkot.popularmovies.MainActivity;
import com.example.tomaszkot.popularmovies.MovieDetail;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 *  These utilities will be used to communicate with the themoviedb server
 */

public class NetworkUtils {

    //TODO Please insert Your Api Key
    private static final String myApiKey = "....";

    private static final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private final static String KEY = "api_key";
    private final static String POPULAR_PATH = "popular";
    private final static String TOP_RATED_PATH = "top_rated";
    private final static String LANGUAGE = "language";
    private final static String ENGLISH_LANG = "en-US";

    public static String getDataFromMoveDB(int sortOrder){
        URL urlPath = buildUrl(sortOrder);
        if (urlPath == null){
            return null;
        }
        try {
            return getResponseFromHttpUrl(urlPath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // ********************************************************************************************* Built URL
    private static URL buildUrl(int sortOrder) {
        // Path for Popular or Top rated movies
        String path;
        if (sortOrder == MainActivity.POPULAR) {
            path = POPULAR_PATH;
        } else {
            path = TOP_RATED_PATH;
        }

        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendPath(path)
                .appendQueryParameter(KEY, myApiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.d("QueryMy", "URL: " + url);
        return url;
    }

    // ********************************************************************************************* Built URL For Detail Movie (REWIEW or TRAILERS)
    public static URL buildUrlForDetailMovie(int sortOrder, int id) {
        String type = null;
        if (sortOrder == MovieDetail.TRAILERS_LOADER) {
            type = "videos";
        } else  if (sortOrder == MovieDetail.REVIEWS_LOADER){
            type = "reviews";
        }

        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendPath(Integer.toString(id))
                .appendPath(type)
                .appendQueryParameter(KEY, myApiKey)
                .appendQueryParameter(LANGUAGE, ENGLISH_LANG)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.d("QueryMy", "URL: " + url);
        return url;
    }


    // ********************************************************************************************** Result from the HTTP response
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }


    // ********************************************************************************************** Check Internet Connection
    public static boolean checkInternetConnect(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService
                (Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo netInformation = connectivityManager.getActiveNetworkInfo();
        // If there is a network connection, then fetch data//
        return netInformation != null && netInformation.isConnected();
    }
}
