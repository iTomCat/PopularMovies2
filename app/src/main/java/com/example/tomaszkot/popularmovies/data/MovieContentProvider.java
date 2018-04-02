package com.example.tomaszkot.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.tomaszkot.popularmovies.models.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Movie Content Provider
 */

public class MovieContentProvider extends ContentProvider {
    private static final String TAG = "DB_MOVIE";
    private MovieDbHelper mMovieDBHelper;
    public static final int FAVORITES = 100;
    public static final int FAVORITES_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieDBHelper = new MovieDbHelper(context);
        return true;
    }

    // --------------------------------------------------------------------------------------------- URI Matcher
    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // Directory
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_FAVORITES, FAVORITES);

        // Single Item
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_FAVORITES + "/#",
                FAVORITES_WITH_ID);

        return uriMatcher;
    }



     // -------------------------------------------------------------------------------------------- Query
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mMovieDBHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match) {
            case FAVORITES:
                retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        assert getContext() != null;
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    // --------------------------------------------------------------------------------------------- Insert
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri addMovieUri;

        switch(match) {
            case FAVORITES:
                long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);

                if (id > 0){
                    addMovieUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
                    assert  getContext() != null;
                    getContext().getContentResolver().notifyChange(addMovieUri, null);
                    Log.v(TAG, "Insert row Successful" + addMovieUri);
                }else{
                    throw new android.database.SQLException("Failed to insert a row " + uri);
                }
                break;

            default:
                Log.e(TAG, "Failed to insert a row");
                throw new UnsupportedOperationException("Unknown URI " + uri);
        }

        return addMovieUri;
    }

    // --------------------------------------------------------------------------------------------- Delete
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int movieDeleted;

        switch (match) {
            case FAVORITES_WITH_ID:
                // Get the task ID from the URI path
                String id = uri.getPathSegments().get(1);
                Log.d("DeleteTest", "pos: "  + id);
                // Use selections/selectionArgs to filter for this ID
                movieDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (movieDeleted != 0) {
            assert getContext() != null;
            getContext().getContentResolver().notifyChange(uri, null);
        }

        Log.d("DeleteTest", "movieDeleted: "  + movieDeleted);
        return movieDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }


    // ********************************************************************************************* Making List from Cursor
    public static List<Movie> movieListFromCursor (Cursor cursor){
        List<Movie> mMovieList = new ArrayList<>();

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Movie movie = new Movie();

            int id = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID);
            movie.setID(cursor.getInt(id));

            int title = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
            movie.setTitle(cursor.getString(title));

            int release = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE);
            movie.setReleaseDate(cursor.getString(release));

            int poster = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER);
            movie.setPosterPath(cursor.getString(poster));

            int vote = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE);
            movie.setVoteAverage(cursor.getString(vote));

            int overview = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
            movie.setPlotSynopsis(cursor.getString(overview));

            mMovieList.add(movie);
        }

        return mMovieList;
    }
}
