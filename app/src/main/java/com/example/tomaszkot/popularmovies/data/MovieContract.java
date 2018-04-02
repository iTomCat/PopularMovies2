package com.example.tomaszkot.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Database and ContentProvider Contract
 */

public class MovieContract {

    // -------------------------------------------------------------- Content Provider URI constants
    static final String AUTHORITY = "com.example.tomaszkot.popularmovies";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    static final String PATH_FAVORITES = "favorites";

    public static final class MovieEntry implements BaseColumns {

        // ----------------------------------------------------------------------- Built Content URI
        public static final Uri CONTENT_URI=
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        // -------------------------------------------------------------------------------- Conatsns
        static final String TABLE_NAME = "favorites";

        public static final String COLUMN_ID = "movie_id";
        public static final String COLUMN_TITLE = "original_title";
        public static final String COLUMN_RELEASE = "release_date";
        public static final String COLUMN_POSTER = "poster_path";
        public static final String COLUMN_VOTE = "vote_average";
        public static final String COLUMN_OVERVIEW = "overview";
    }
}
